package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Kullanıcı Kayıt Etme Endpoint'i (POST)
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Tüm Kullanıcıları Getirme Endpoint'i (GET)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}