package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.RegisterRequest;
import com.randevukuafor.randevu_sistemi.exception.EmailAlreadyExistsException;
import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.model.Role; // Role enum'ını doğrudan import ettik
import com.randevukuafor.randevu_sistemi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //Bağımlılık eklendi

    // Constructor Injection (Spring ikisini de otomatik bağlayacak)
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Yeni kullanıcı kaydetme metodu
    public User createUser(RegisterRequest request) {
        // 1. Kontrol: DTO'dan gelen email üzerinden benzersizlik kontrolü yapılıyor
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Bu email adresi zaten kullanımda!");
        }

        // 2. Mapping (Eşleme): DTO verilerini yeni bir User Entity nesnesine aktarıyoruz
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());

        //Dünyanın en güvenli şifreleme algoritmalarından biri olan BCrypt ile hash'liyoruz
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Varsayılan olarak her yeni kayıt olanı CUSTOMER (Müşteri) rolüyle başlatalım
        user.setRole(Role.CUSTOMER);

        // 3. Kayıt: Tamamen hazır olan Entity nesnesini veritabanına gönderiyoruz
        return userRepository.save(user);
    }

    // Tüm kullanıcıları listeleme (Test amaçlı)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}