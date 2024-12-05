package com.example.GoldenNest.util.annotation;

import com.example.GoldenNest.config.security.JwtTokenService;
import com.example.GoldenNest.util.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class LoginCheckAspect {
    @Autowired
    private JwtTokenService jwtTokenService;

    @Before("@annotation(CheckLogin)")
    public ResponseEntity<?> checkLogin(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Không tìm thấy token trong header hoặc token không hợp lệ.");
            }

            String token = authHeader.substring(7);

            // Kiểm tra định dạng của token
            if (!jwtTokenService.isTokenFormatValid(token)) {
                throw new UnauthorizedException("Token không đúng định dạng.");
            }

            // Kiểm tra xem token đã hợp lệ hay không
            if (!jwtTokenService.isTokenValid(token)) {
                throw new UnauthorizedException("Token không hợp lệ hoặc không có thông tin.");
            }

            // Kiểm tra xem token đã hết hạn chưa
            if (jwtTokenService.isTokenExpired(token)) {
                throw new UnauthorizedException("Token đã hết hạn.");
            }

            // Nếu tất cả các kiểm tra đều thành công, trả về thông tin người dùng
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                return ResponseEntity.ok(currentUserName);
            }
            throw new UnauthorizedException("Không có thông tin người dùng trong phiên đăng nhập.");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}
