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
    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service savedService = hairdresserService.createService(service);
        return ResponseEntity.ok(savedService);
    }

    // 2. Belirli Bir Dükkanın Hizmetlerini Listeleme API'ı (GET http://localhost:8080/api/services/shop/{shopId})
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Service>> getServicesByShop(@PathVariable Long shopId) {
        List<Service> services = hairdresserService.getServicesByShop(shopId);
        return ResponseEntity.ok(services);
    }
}