package com.randevukuafor.randevu_sistemi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        // Giriş ve kayıt işlemlerini herkes yapabilir
                        .requestMatchers("/api/auth/**").permitAll()

                        // Sadece BERBER rolü olanlar yeni dükkan ekleyebilir veya silebilir!
                        .requestMatchers(HttpMethod.POST, "/api/shops/**").hasRole("BARBER")
                        .requestMatchers(HttpMethod.DELETE, "/api/shops/**").hasRole("BARBER")

                        // Randevu alma veya dükkanları listeleme işlemlerini hem MÜŞTERİ hem BERBER yapabilir
                        .requestMatchers("/api/appointments/**").hasAnyRole("CUSTOMER", "BARBER")

                        // Geri kalan her şey giriş yapmış olmayı zorunlu kılar
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