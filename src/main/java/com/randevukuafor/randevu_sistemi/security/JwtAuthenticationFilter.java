package com.randevukuafor.randevu_sistemi.security;

import com.randevukuafor.randevu_sistemi.exception.ResourceNotFoundException;
import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Herkese açık yolları kontrol et
        String path = request.getRequestURI();
        if (path.startsWith("/auth/") || path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. authHeader değişkenini metot seviyesinde tanımladık
        final String authHeader = request.getHeader("Authorization");

        // 3. Token kontrolü
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. try-catch ile güvenli hale getirdik
        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

                if (jwtService.isTokenValid(jwt, user.getEmail())) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, List.of(authority)
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Hata olsa bile zinciri devam ettiriyoruz
            e.printStackTrace();
            System.out.println("JWT doğrulama hatası: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}