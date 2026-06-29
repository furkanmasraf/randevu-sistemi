package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.RegisterRequest;
import com.randevukuafor.randevu_sistemi.exception.EmailAlreadyExistsException;
import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.model.Role;
import com.randevukuafor.randevu_sistemi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Bu email adresi zaten kullanımda!");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Gelen String rol bilgisini Enum yapısına dinamik olarak dönüştürüyoruz
        try {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            user.setRole(Role.CUSTOMER);
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}