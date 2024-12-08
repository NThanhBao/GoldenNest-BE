package com.example.GoldenNest.util.annotation;

import com.example.GoldenNest.config.security.JwtTokenService;
import com.example.GoldenNest.util.exception.UnauthorizedException;
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
    public ResponseEntity<?> checkAdmin(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authHeader = request.getHeader("Authorization");

            // Kiểm tra xem token có trong header hay không
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Không tìm thấy token trong header hoặc token không hợp lệ.");
            }

            String token = authHeader.substring(7);

            // Lấy thông tin người dùng từ token (ví dụ: username hoặc userId)
            String currentUserName = jwtTokenService.extractUsername(token);

            // Tạo Authentication từ thông tin người dùng
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Kiểm tra xem người dùng có quyền admin hay không
            if (!authentication.getAuthorities().contains(AuthorityUtils.createAuthorityList("ROLE_ADMIN").get(0))) {
                throw new UnauthorizedException("Bạn không có quyền truy cập (Admin required).");
            }

            // Nếu người dùng có quyền admin, cho phép tiếp tục
            return ResponseEntity.ok(currentUserName);

        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("Có lỗi xảy ra!");
        }
    }
}
