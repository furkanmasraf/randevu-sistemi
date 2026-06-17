package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    // Test amaçlı hızlıca token üreten endpoint
    @GetMapping("/generate-token")
    public ResponseEntity<Map<String, String>> generateTestToken(@RequestParam String email) {
        String token = jwtService.generateToken(email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}