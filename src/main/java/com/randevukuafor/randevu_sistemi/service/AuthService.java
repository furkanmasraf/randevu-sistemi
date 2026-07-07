package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.LoginRequest;
import com.randevukuafor.randevu_sistemi.dto.RegisterRequest;
import com.randevukuafor.randevu_sistemi.exception.EmailAlreadyExistsException;
import com.randevukuafor.randevu_sistemi.model.Role;
import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.repository.UserRepository;
import com.randevukuafor.randevu_sistemi.security.JwtService;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    // Kayıt Olma Metodu (Dinamik Rol Destekli)
    @Retry(name = "authRetry", fallbackMethod = "fallbackRegister")
    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Bu email adresi zaten kullanımda!");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber()); // DTO'daki yeni isimlendirmeyle eşitlendi

        // Ön yüzden gelen String rolü (CUSTOMER/BARBER) Enum tipine dinamik olarak dönüştürüyoruz
        try {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            user.setRole(Role.CUSTOMER); // Hata durumunda güvenli liman olarak CUSTOMER atıyoruz
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "Kullanıcı başarıyla kaydedildi!";
    }

    // Resilience4j Fallback Metodu
    public String fallbackRegister(RegisterRequest request, Exception e) {
        System.out.println("Yakalanan Hata: " + e.getMessage());
        return "Şu anda sistemlerimizde geçici bir yoğunluk yaşanıyor. Lütfen birkaç dakika sonra tekrar deneyiniz.";
    }

    // Kullanıcı Giriş Metodu
    public Map<String, String> login(LoginRequest request) {
        // 1. Spring Security'nin kendi doğrulama mekanizmasını tetikle
        // Bu satır senin SecurityConfig'deki Provider'ını ve PasswordEncoder'ını kullanır
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Kullanıcıyı getir
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("E-posta veya şifre hatalı!"));

        // 3. Token oluştur
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());

        String token = jwtService.generateToken(extraClaims, user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", String.valueOf(user.getId()));
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", user.getRole().name());

        return response;
    }
}