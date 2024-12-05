package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.AuthDTO;
import com.example.GoldenNest.model.dto.UserDTO;
import com.example.GoldenNest.service.AuthService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Đăng ký người dùng mới
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthDTO registerDTO) {
        return authService.register(registerDTO);
    }

    // Đăng nhập người dùng
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        try {
            authService.login(username, password);
            return ResponseEntity.ok("Đăng nhập thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    // Phương thức cập nhật thông tin người dùng
    @CheckLogin
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserDTO updatedUserDto) {
        return authService.updateUser(updatedUserDto);
    }

}
