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
import org.springframework.security.authentication.BadCredentialsException; // Standart güvenlik hatası
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

    @Retry(name = "authRetry", fallbackMethod = "fallbackRegister")
    public String register(RegisterRequest request) {
        // Eski RuntimeException yerine kendi yazdığım özel hatayı fırlatıyorum..
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Bu email adresi zaten kullanımda!");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());
        user.setRole(Role.CUSTOMER);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "Kullanıcı başarıyla kaydedildi!";
    }

    public String fallbackRegister(RegisterRequest request, Exception e) {
        System.out.println("Yakalanan Hata: " + e.getMessage());
        return "Şu anda sistemlerimizde geçici bir yoğunluk yaşanıyor. Lütfen birkaç dakika sonra tekrar deneyiniz.";
    }

    // kullanıcı giriş metodu
    public Map<String, String> login(LoginRequest request) {
        // Güvenlik gereği e-posta veya şifre yanlışsa hep aynı genel hatayı döneriz (Kötü niyetli taramaları önlemek için)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("E-posta veya şifre hatalı!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("E-posta veya şifre hatalı!");
        }

        // DEFERDEKİ MADDEYİ ÇÖZÜYORUZ: Token içine claims (ekstra veri) hazırlığı
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name()); // Token gövdesine rolleri (CUSTOMER/BARBER) yazdık

        // Düz metot yerine, hazırladığımız claims haritasını alan aşırı yüklenmiş (overloaded) metodu çağırıyoruz
        String token = jwtService.generateToken(extraClaims, user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", user.getRole().name());

        return response;
    }
}