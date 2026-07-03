package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.service.HairdresserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private HairdresserService hairdresserService;

    // 1. Hizmet Oluşturma API'ı (POST http://localhost:8080/api/services)
    @PostMapping("/shop/{shopId}")
    public ResponseEntity<?> createService(@RequestBody Service service, @PathVariable Long shopId) {
        hairdresserService.createService(service, shopId);

        // ASLA 'savedService' nesnesini dönme, sadece mesaj dön:
        return ResponseEntity.ok(Map.of("message", "Hizmet başarıyla eklendi"));
    }

    // 2. Belirli Bir Dükkanın Hizmetlerini Listeleme API'ı (GET http://localhost:8080/api/services/shop/{shopId})
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<?> getServicesByShop(@PathVariable Long shopId) {
        List<Service> services = hairdresserService.getServicesByShop(shopId);

        // Sadece gerekli alanları içeren basit bir yapıya dönüştür (Mapping)
        List<Map<String, Object>> response = services.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("name", s.getName());
            map.put("price", s.getPrice());
            // shop bilgisini burada koyma!
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}