package com.example.GoldenNest.service;

import com.example.GoldenNest.model.entity.OTPs;
import com.example.GoldenNest.model.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;


public interface OTPsService {

    void saveOTP(Users user, String mail, String otp, Timestamp expirationTime);

    String generateOTP();

    String generateOTPAndSendEmail(String email);

    ResponseEntity<String> validateOTP(String mail, String otp);

    Page<OTPs> findAllUsersWithOTP(Pageable pageable);

}
