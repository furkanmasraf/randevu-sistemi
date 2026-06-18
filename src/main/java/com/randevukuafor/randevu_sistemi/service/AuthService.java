package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.LoginRequest;
import com.randevukuafor.randevu_sistemi.dto.RegisterRequest;
import com.randevukuafor.randevu_sistemi.model.Role;
import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.repository.UserRepository;
import com.randevukuafor.randevu_sistemi.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // 1. KULLANICI KAYIT METODU
    public String register(RegisterRequest request) {
        // Email zaten alınmış mı kontrolü
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kullanımda!");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());
        user.setRole(Role.valueOf("CUSTOMER")); // Varsayılan müşteri rolü

        // ŞİFREYİ BCrypt İLE MASKELİYORUZ
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return "Kullanıcı başarıyla kaydedildi!";
    }

    // 2. KULLANICI GİRİŞ METODU
    public Map<String, String> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("E-posta veya şifre hatalı!"));

        // Girilen şifre ile DB'deki hash'lenmiş şifre uyuşuyor mu?
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("E-posta veya şifre hatalı!");
        }

        // Giriş başarılıysa JWT token üretip dönüyoruz
        String token = jwtService.generateToken(user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", String.valueOf(user.getRole()));

        return response;
    }
}