package com.example.GoldenNest.controller;

import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.AuthService;
import com.example.GoldenNest.service.OTPsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/reset-password")
public class OTPsController {
    private final OTPsService otpService;
    private final AuthService authService;
    private final UsersRepository usersRepository;

    @Autowired
    public OTPsController(OTPsService otpService, UsersRepository usersRepository, AuthService authService) {
        this.otpService = otpService;
        this.usersRepository = usersRepository;
        this.authService = authService;
    }

    @PostMapping("/email/send")
    public ResponseEntity<String> sendOTP(@RequestParam String email) {
        if (!usersRepository.existsByMail(email)) {
            return new ResponseEntity<>("Email does not exist in the system.", HttpStatus.BAD_REQUEST);
        }
        String otp = otpService.generateOTPAndSendEmail(email);
        return new ResponseEntity<>("OTP sent successfully : " + otp, HttpStatus.OK);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmOTP(@RequestParam String mail,
                                             @RequestParam String otp,
                                             @RequestParam String newPassword) {
        ResponseEntity<String> validationResponse = otpService.validateOTP(mail, otp);
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }
        ResponseEntity<String> response;
        response = authService.updatePassword(mail, newPassword);
        return response;
    }
}
