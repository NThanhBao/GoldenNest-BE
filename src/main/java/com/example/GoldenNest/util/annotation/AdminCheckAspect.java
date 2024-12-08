package com.example.GoldenNest.util.annotation;

import com.example.GoldenNest.config.security.JwtTokenService;
import com.example.GoldenNest.util.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AdminCheckAspect {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Before("@annotation(CheckAdmin)")
    public void checkAdmin(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authHeader = request.getHeader("Authorization");

            // Kiểm tra header Authorization
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Không tìm thấy token trong header hoặc token không hợp lệ.");
            }

            String token = authHeader.substring(7);

            // Kiểm tra token
            if (!jwtTokenService.isTokenFormatValid(token) || !jwtTokenService.isTokenValid(token)) {
                throw new UnauthorizedException("Token không hợp lệ.");
            }

            // Kiểm tra quyền Admin
            Claims claims = jwtTokenService.extractAllClaims(token);
            String role = claims.get("role", String.class);

            if (role == null || !role.equals("ADMIN")) {
                throw new UnauthorizedException("Bạn không có quyền truy cập (Admin required).");
            }

        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("Có lỗi xảy ra trong quá trình kiểm tra quyền.");
        }
    }
}
