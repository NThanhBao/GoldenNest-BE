package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.config.security.CustomUserDetailsService;
import com.example.GoldenNest.config.security.JwtTokenService;
import com.example.GoldenNest.model.dto.AuthDTO;
import com.example.GoldenNest.model.entity.Enum.EnableType;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.AuthService;
import com.example.GoldenNest.util.exception.ConflictException;
import com.example.GoldenNest.util.exception.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceImpl implements AuthService {

    public static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(UsersRepository registerRepository, BCryptPasswordEncoder encoder, JwtTokenService jwtTokenService) {
        this.userRepository = registerRepository;
        this.encoder = encoder;
        this.jwtTokenService = jwtTokenService;
    }

    //    Lấy thông tin người dùng theo username.
    @Override
    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Users users = userRepository.findByUsername(username);
        if (users == null) {
            logger.error("Không tìm thấy người dùng với tên đăng nhập: {}", username);
            throw new UsernameNotFoundException(username);
        }
        if (users.getEnableType() == EnableType.FALSE) {
            logger.error("Tài khoản của người dùng '{}' đã bị vô hiệu hóa.", username);
            throw new DisabledException("Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ với quản trị viên để biết thêm chi tiết.");
        }
        logger.info("Đã tìm thấy người dùng: {}", username);
        return new CustomUserDetailsService(users);
    }

    @Override
    public String login(String username, String password) {
        try {
            UserDetails userDetails = loadUserByUsername(username);

            if (!encoder.matches(password, userDetails.getPassword())) {
                logger.error("--LOGIN FAILED FOR USER: {}", username);
                throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng");
            }

            logger.info("--LOGIN SUCCESSFUL FOR USER-- : {}", username);

            // Tạo JWT token
            String token = jwtTokenService.generateToken(username);

            // Ghi lại token cho việc gỡ lỗi (Lưu ý khi sử dụng trong môi trường sản xuất)
            logger.info("Token cho người dùng {}: {}", username, token);

            // Trả về token
            return token;
        } catch (UsernameNotFoundException ex) {
            logger.error("--LOGIN FAILED FOR USER: {}", username);
            throw new UsernameNotFoundException("Tài khoản không tồn tại");
        }
    }

    @Override
    public String register(AuthDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            logger.warn("Tên người dùng '{}' đã tồn tại", registerDTO.getUsername());
            throw new UsernameNotFoundException("Tên người dùng đã tồn tại.");
        }

        if (isUserExists(registerDTO)) {
            throw new ConflictException("Thông tin đã tồn tại.");
        }

        if (!isPasswordValid(registerDTO.getPassword())) {
            logger.warn("Mật khẩu không đáp ứng yêu cầu: mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
            throw new InvalidRequestException("Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
        }

        Users user = new Users();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encoder.encode(registerDTO.getPassword()));
        user.setMail(registerDTO.getMail());
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setGender(registerDTO.isGender());
        user.setDateOfBirth(registerDTO.getDateOfBirth());
        user.setAvatar(registerDTO.getAvatar());

        userRepository.save(user);

        logger.info("User '{}' created successfully", user.getUsername());
        return user.getUsername();
    }

    @Override
    public ResponseEntity<String> updateUser(AuthDTO updatedUserDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users existingUser = userRepository.findByUsername(currentUsername);

        if (existingUser == null) {
            logger.warn("Không tìm thấy người dùng với username: '{}'", currentUsername);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }

        // Cập nhật thông tin người dùng
        existingUser.setFirstName(updatedUserDto.getFirstName());
        existingUser.setLastName(updatedUserDto.getLastName());
        existingUser.setGender(updatedUserDto.isGender());
        existingUser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        existingUser.setDateOfBirth(updatedUserDto.getDateOfBirth());
        existingUser.setAddress(updatedUserDto.getAddress());
        existingUser.setMail(updatedUserDto.getMail());

        // Lưu thông tin cập nhật vào cơ sở dữ liệu
        userRepository.save(existingUser);

        logger.info("Thông tin của người dùng '{}' đã được cập nhật thành công", currentUsername);
        return ResponseEntity.ok("Cập nhật thành công thông tin người dùng");
    }

    @Override
    public ResponseEntity<String> deleteUser() {
        // Lấy tên người dùng hiện tại từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Tìm người dùng trong cơ sở dữ liệu
        Users existingUser = userRepository.findByUsername(currentUsername);

        if (existingUser == null) {
            logger.warn("Không tìm thấy người dùng với username: '{}'", currentUsername);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }

        // Chuyển enableType thành FALSE để vô hiệu hóa tài khoản
        existingUser.setEnableType(EnableType.FALSE);
        userRepository.save(existingUser);

        logger.info("Tài khoản của người dùng '{}' đã bị vô hiệu hóa", currentUsername);
        return ResponseEntity.ok("Tài khoản đã bị vô hiệu hóa");
    }

    @Override
    public ResponseEntity<String> updatePassword(String email, String newPassword) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("Người dùng với email '{}' không tồn tại", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại.");
        }
        String encryptedPassword = encoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        logger.info("Mật khẩu của người dùng với email '{}' đã được cập nhật thành công", email);
        return ResponseEntity.ok("Mật khẩu cập nhật thành công.");
    }


    private boolean isUserExists(AuthDTO registerDTO) {
        return userRepository.existsByUsername(registerDTO.getUsername()) ||
                userRepository.existsByMail(registerDTO.getMail()) ||
                userRepository.existsByPhoneNumber(registerDTO.getPhoneNumber());
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8 &&
                password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*") && password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
