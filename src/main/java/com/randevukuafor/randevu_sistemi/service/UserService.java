package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor Injection (Spring otomatik bağlayacak)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Yeni kullanıcı kaydetme metodu
    public User createUser(User user) {
        // İleride buraya "Email zaten kayıtlı mı?" kontrolü ekleyeceğiz
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kullanımda!");
        }
        return userRepository.save(user);
    }

    // Tüm kullanıcıları listeleme (Test amaçlı)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}