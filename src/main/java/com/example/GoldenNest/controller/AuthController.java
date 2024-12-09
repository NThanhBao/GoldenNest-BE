package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.AuthDTO;
import com.example.GoldenNest.service.AuthService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        try {
            authService.register(registerDTO);
            return ResponseEntity.ok("Đăng ký thành công với username: " + registerDTO.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Đăng nhập người dùng và trả về chỉ JWT token
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        try {
            String token = authService.login(username, password);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Cập nhật thông tin người dùng
    @CheckLogin
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody AuthDTO updatedUserDto) {
        return authService.updateUser(updatedUserDto);
    }

    // Xóa người dùng (vô hiệu hóa tài khoản)
    @CheckLogin
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser() {
        return authService.deleteUser();
    }

}
