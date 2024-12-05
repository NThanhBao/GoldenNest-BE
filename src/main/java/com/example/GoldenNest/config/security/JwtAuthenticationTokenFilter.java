package com.example.GoldenNest.config.security;

import com.example.GoldenNest.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private final AuthService authService;

    public JwtAuthenticationTokenFilter(JwtTokenService jwtTokenService, AuthService authService) {
        this.jwtTokenService = jwtTokenService;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                String username = jwtTokenService.extractUsername(jwt);
                Authentication securityContextHolder = SecurityContextHolder.getContext().getAuthentication();
                if (username != null && securityContextHolder != null) {
                    UserDetails userDetails = authService.loadUserByUsername(username);
                    if (jwtTokenService.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        logger.error("Xác thực không thành công: Token không hợp lệ hoặc không tìm thấy người dùng.");
                    }
                } else {
                    logger.error("Xác thực không thành công: Không tìm thấy người dùng hoặc người dùng đã được xác thực trước đó.");
                }
            }
        } catch (Exception ex) {
            logger.error("Xác thực không thành công: " + ex.getMessage(), ex);
        }
        filterChain.doFilter(request, response);
    }

    //    lấy JWT từ tiêu đề Authorization của yêu cầu HTTP.
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;

    }
}