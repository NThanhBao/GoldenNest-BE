package com.example.GoldenNest.model.dto;

import com.example.GoldenNest.model.entity.Enum.EnableType;
import com.example.GoldenNest.model.entity.Enum.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private String id;

    private String username;

    private String password;

    private String mail;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private EnableType enableType;

    private RoleType roleType;

    private boolean gender;

    private Timestamp dateOfBirth;

    private String address;

    private String avatar;
}