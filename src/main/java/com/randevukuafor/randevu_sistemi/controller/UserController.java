package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.dto.RegisterRequest;
import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Kullanıcı Kayıt Etme Endpoint'i (POST)
    // UserController.java içindeki o kırmızı metodu şununla değiştir veya tamamen sil:
    @PostMapping("/register")
    public User registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.createUser(registerRequest);
    }

    // Tüm Kullanıcıları Getirme Endpoint'i (GET)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id); // UserService'inde böyle bir metodun olmalı
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}