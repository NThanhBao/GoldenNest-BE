package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.AuthDTO;
import com.example.GoldenNest.model.dto.UserDTO;
import com.example.GoldenNest.model.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    UserDetails loadUserByUsername(String username);

    UserDetails login(String username, String password);

    ResponseEntity<String> register(AuthDTO registerDTO);

    Users getUserByUsername(String username);

    ResponseEntity<String> updateUser(AuthDTO updatedUserDto);

    ResponseEntity<String> deleteUser(String username);
}
