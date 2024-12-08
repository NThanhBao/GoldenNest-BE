package com.example.GoldenNest.model.dto;

import com.example.GoldenNest.model.entity.Users;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class OTPsDto {

    private String id;

    private Users userId;

    private String mail;

    private String otp;

    private Timestamp createAt;

    private Timestamp expirationTime;

    @Override
    public String toString() {
        return "OTPsDto{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", mail='" + mail + '\'' +
                ", otp='" + otp + '\'' +
                ", createAt=" + createAt +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
