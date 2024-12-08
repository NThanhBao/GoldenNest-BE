package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.entity.OTPs;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.OTPsRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.EmailService;
import com.example.GoldenNest.service.OTPsService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;

@Service
public class OTPsServiceImpl implements OTPsService, EmailService {

    private final JavaMailSender javaMailSender;
    private final OTPsRepository otpRepository;
    private final UsersRepository userRepository;
    private static final int OTP_EXPIRATION_MINUTES = 5;
    private static final Logger logger = LoggerFactory.getLogger(OTPsServiceImpl.class);

    public OTPsServiceImpl(JavaMailSender javaMailSender, OTPsRepository otpRepository, UsersRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        logger.info("Đang gửi email đến: {}", to);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessage.setContent(content, "text/html; charset=UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            javaMailSender.send(mimeMessage);
            logger.info("Email đã được gửi thành công đến: {}", to);
        } catch (MessagingException e) {
            logger.error("Gửi email đến '{}' thất bại.", to, e);
            throw new RuntimeException("Gửi email thất bại.");
        }
    }

    @Override
    public String generateOTPAndSendEmail(String email) {
        logger.info("Đang tạo mã OTP và gửi email xác nhận cho người dùng với email: {}", email);
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("Không tìm thấy người dùng với email: {}", email);
            throw new RuntimeException("Không tìm thấy người dùng với email: " + email);
        }
        String otp = generateOTP();
        String fullName = user.getLastName();
        String subject = "OTP Confirmation";
        String emailContent = generateOTPContent(fullName, otp);
        sendEmail(email, subject, emailContent);
        saveOTP(user, email, otp, calculateExpirationTime());
        logger.info("Mã OTP đã được tạo và gửi thành công đến email: {}", email);
        return otp;
    }

    @Override
    public void saveOTP(Users user, String mail, String otp, Timestamp expirationTime) {
        logger.info("Đang lưu mã OTP cho email: {}", mail);
        OTPs otpEntity = new OTPs();
        otpEntity.setUserId(user);
        otpEntity.setMail(mail);
        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(expirationTime);
        otpRepository.save(otpEntity);
        logger.info("Mã OTP đã được lưu thành công cho email: {}", mail);
    }

    @Override
    public String generateOTP() {
        logger.info("Đang tạo mã OTP mới");
        final String OTP_CHARACTERS = "0123456789";
        final int OTP_LENGTH = 6;
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(OTP_CHARACTERS.charAt(random.nextInt(OTP_CHARACTERS.length())));
        }
        logger.info("Mã OTP mới đã được tạo: {}", otp.toString());
        return otp.toString();
    }

    @Override
    public ResponseEntity<String> validateOTP(String mail, String otp) {
        logger.info("Xác thực OTP cho email: {}", mail);
        OTPs otpEntity = otpRepository.findByMailAndOtp(mail, otp);
        if (otpEntity == null) {
            logger.warn("OTP không hợp lệ cho email: {}", mail);
            return new ResponseEntity<>("OTP không hợp lệ.", HttpStatus.BAD_REQUEST);
        }
        if (otpEntity.isUsed()) {
            logger.warn("OTP đã được sử dụng cho email: {}", mail);
            return new ResponseEntity<>("OTP đã được sử dụng.", HttpStatus.BAD_REQUEST);
        }
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp expirationTime = otpEntity.getExpirationTime();
        if (currentTime.after(expirationTime)) {
            logger.warn("OTP đã hết hạn cho email: {}", mail);
            return new ResponseEntity<>("OTP đã hết hạn.", HttpStatus.BAD_REQUEST);
        }
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
        logger.info("OTP đã được xác thực thành công cho email: {}", mail);
        return new ResponseEntity<>("OTP hợp lệ.", HttpStatus.OK);
    }

    @Override
    public Page<OTPs> findAllUsersWithOTP(Pageable pageable) {
        return otpRepository.findAllWithUser(pageable);
    }

    private String generateOTPContent(String fullName, String otp) {
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<html><body style='font-family: Arial, sans-serif;'>");
        emailContent.append("<h2 style='color: #333333;'>Xin chào ").append(fullName).append(",</h2>");
        emailContent.append("<p style='color: #333333;'>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu từ bạn. Dưới đây là mã OTP của bạn.</p>");
        emailContent.append("<p style='color: #333333;'><strong>Mã OTP của bạn:</strong> ").append(otp).append("</p>");
        emailContent.append("<p style='color: #333333;'>Vui lòng sử dụng mã OTP này để đặt lại mật khẩu của bạn.</p>");
        emailContent.append("<p style='color: #333333;'>Lưu ý: Không chia sẻ mã OTP này với bất kỳ ai.</p>");
        emailContent.append("<p style='color: #333333;'>Trân trọng,<br>");
        emailContent.append("Đội ngũ hỗ trợ GoldenNest</p>");
        emailContent.append("<p style='color: #333333;'>Email: ntbs.customer.services@gmail.com<br>");
        emailContent.append("Hotline: 1900 123 456<br>");
        emailContent.append("Thời gian làm việc: Thứ Hai - Thứ Sáu, 8:00 - 17:00</p>");
        emailContent.append("</body></html>");
        return emailContent.toString();
    }

    private Timestamp calculateExpirationTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + (OTP_EXPIRATION_MINUTES * 60 * 1000);
        return new Timestamp(expirationTimeMillis);
    }
}