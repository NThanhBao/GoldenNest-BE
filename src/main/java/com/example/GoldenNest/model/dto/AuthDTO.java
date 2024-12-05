package com.example.GoldenNest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class AuthDTO {

    private String username;

    private String password;

    private String mail;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private boolean gender;

    private Timestamp dateOfBirth;

    private String address;

    private String avatar;
}