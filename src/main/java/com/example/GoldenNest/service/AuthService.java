package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.AuthDTO;
import com.example.GoldenNest.model.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    UserDetails loadUserByUsername(String username);

    String login(String username, String password);

    String register(AuthDTO registerDTO);

    Users getUserByUsername(String username);

    ResponseEntity<String> updateUser(AuthDTO updatedUserDto);

    ResponseEntity<String> deleteUser();

    ResponseEntity<String> updatePassword(String email, String newPassword);
}
