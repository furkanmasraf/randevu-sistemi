package com.randevukuafor.randevu_sistemi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. REST API geliştirdiğimiz için CSRF korumasını kapatıyoruz
                .csrf(csrf -> csrf.disable())

                // 2. Hangi isteklere izin verilecek, hangileri kilitlenecek ayarlıyoruz
                .authorizeHttpRequests(auth -> auth
                        // Giriş ve kayıt endpoint'lerini herkese açacağız (Şimdilik test için auth paketini serbest bırakalım)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Geri kalan TÜM endpoint'leri (Randevular, Dükkanlar vb.) JWT şifresine bağlıyoruz!
                        .anyRequest().authenticated()
                )

                // 3. JWT kullandığımız için session (oturum) yönetimini STATELESS (durumsuz) yapıyoruz
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Kendi yazdığımız JWT filtresini, Spring'in standart UsernamePassword filtresinin önüne ekliyoruz
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Şifreleri BCrypt ile koruma altına alıyoruz
    }
}