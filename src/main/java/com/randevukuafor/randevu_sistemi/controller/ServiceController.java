package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.service.HairdresserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private HairdresserService hairdresserService;

    // 1. Hizmet Oluşturma API'ı (POST http://localhost:8080/api/services)
    @PostMapping("/shop/{shopId}")
    public ResponseEntity<?> createService(@RequestBody Service service, @PathVariable Long shopId) {
        try {
            // Gelen veriyi konsola yazdıralım, gerçekten dolu geliyor mu görelim
            System.out.println("Gelen Service verisi: " + service);
            System.out.println("Gelen Shop ID: " + shopId);

            Service savedService = hairdresserService.createService(service, shopId);
            return ResponseEntity.ok(savedService);

        } catch (Exception e) {
            // Hatanın detaylı dökümünü IntelliJ konsolunda göreceğiz
            e.printStackTrace();
            return ResponseEntity.status(500).body("Hata: " + e.getMessage());
        }
    }

    // 2. Belirli Bir Dükkanın Hizmetlerini Listeleme API'ı (GET http://localhost:8080/api/services/shop/{shopId})
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<?> getServicesByShop(@PathVariable Long shopId) {
        try {
            return ResponseEntity.ok(hairdresserService.getServicesByShop(shopId));
        } catch (Exception e) {
            e.printStackTrace(); // Hatayı konsola tam detaylı yazdır
            return ResponseEntity.status(500).body("Servisler alınamadı: " + e.getMessage());
        }
    }
}