package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.dto.ShopDTO;
import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.EmployeeRepository;
import com.randevukuafor.randevu_sistemi.repository.ServiceRepository;
import com.randevukuafor.randevu_sistemi.service.ShopService;
import com.randevukuafor.randevu_sistemi.repository.ShopRepository; // Dükkan kayıt işlemleri için eklendi
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ShopController {

    private final ShopService shopService;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;
    private final ShopRepository shopRepository; // Constructor injection için eklendi

    // Tüm bağımlılıklar tek bir Constructor Injection ile güvenli şekilde enjekte edildi
    public ShopController(ShopService shopService,
                          EmployeeRepository employeeRepository,
                          ServiceRepository serviceRepository,
                          ShopRepository shopRepository) {
        this.shopService = shopService;
        this.employeeRepository = employeeRepository;
        this.serviceRepository = serviceRepository;
        this.shopRepository = shopRepository;
    }

    @PostMapping("/register")
    public Shop registerShop(@RequestBody Shop shop) {
        return shopService.createShop(shop);
    }

    @GetMapping
    public List<ShopDTO> getAllShops() {
        return shopService.getAllShops();
    }

    @GetMapping("/filter")
    public List<ShopDTO> getShopsByCity(@RequestParam String city) {
        return shopService.getShopsByCity(city);
    }

    @GetMapping("/{shopId}/employees")
    public List<Employee> getShopEmployees(@PathVariable Long shopId) {
        return employeeRepository.findByShopId(shopId);
    }

    // --- CRITICAL UPDATE: LAZY/Proxy sorununu çözmek için ResponseEntity<List<Map>> yapısına geçildi ---
    @GetMapping("/{shopId}/services")
    public ResponseEntity<List<Map<String, Object>>> getShopServices(@PathVariable Long shopId) {
        List<Service> services = serviceRepository.findByShopId(shopId);

        List<Map<String, Object>> response = services.stream().map(service -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", service.getId());
            map.put("name", service.getName());
            map.put("price", service.getPrice());
            map.put("durationInMinutes", service.getDurationInMinutes());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // --- YENİ EKLENEN OPERASYONEL ENDPOINT'LER ---

    // Sonsuz döngüyü (Infinite Recursion) engellemek için ShopDTO dönen güncel metot
    @GetMapping("/owner/{userId}")
    public ResponseEntity<?> getShopByOwner(@PathVariable Long userId) {
        Optional<Shop> shopOpt = shopRepository.findByOwnerId(userId);

        if (shopOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Dükkan bulunamadı.");
        }

        Shop shop = shopOpt.get();

        // DTO'ya dönüştür (Döngüden kurtulmak için)
        ShopDTO dto = new ShopDTO();
        dto.setId(shop.getId());
        dto.setName(shop.getName());
        dto.setAddressText(shop.getAddressText());
        dto.setCity(shop.getCity());
        dto.setDistrict(shop.getDistrict());
        dto.setStartTime(shop.getStartTime());
        dto.setEndTime(shop.getEndTime());

        return ResponseEntity.ok(dto);
    }

    // Dükkanın Çalışma Saatlerini Güncelleme Endpoint'i
    @PutMapping("/{shopId}/working-hours")
    public ResponseEntity<?> updateWorkingHours(
            @PathVariable Long shopId,
            @RequestBody Map<String, String> hours) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));

        shop.setStartTime(hours.get("startTime"));
        shop.setEndTime(hours.get("endTime"));
        shopRepository.save(shop);

        return ResponseEntity.ok(Map.of("message", "Çalışma saatleri başarıyla güncellendi"));
    }

    // --- CRITICAL UPDATE: Ön yüzün eklenen hizmeti anında Proxy sarmalı olmadan okuyabilmesi sağlandı ---
    @PostMapping("/{shopId}/services")
    public ResponseEntity<?> addServiceToShop(@PathVariable Long shopId, @RequestBody Service service) {
        // 1. Shop'u bul
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));

        // 2. Service nesnesini temizle ve ilişkiyi kur
        service.setShop(shop);

        // 3. Kaydet
        Service savedService = serviceRepository.save(service);

        // 4. Sadece basit bir başarı mesajı veya DTO dön (Entity'nin kendisini DÖNME!)
        return ResponseEntity.ok(Map.of("message", "Hizmet başarıyla eklendi", "id", savedService.getId()));
    }

    // Hizmet Silme Endpoint'i
    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Hizmet bulunamadı"));

        serviceRepository.delete(service);
        return ResponseEntity.ok(Map.of("message", "Hizmet başarıyla silindi"));
    }

    // işletme detayları..
    @GetMapping("/{shopId}/details")
    public ResponseEntity<ShopDTO> getShopDetails(@PathVariable Long shopId) {
        return ResponseEntity.ok(shopService.getShopById(shopId));
    }

    @PutMapping(value = "/{shopId}/update-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShopDTO> updateShopWithImage(
            @PathVariable Long shopId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("shopName") String shopName,
            @RequestParam("phoneNumber") String phoneNumber) throws IOException {

        Shop shop = shopRepository.findById(shopId).orElseThrow();

        // 1. KLASÖR KONTROLÜ VE OLUŞTURMA (Eksik olan kısım burası!)
        String uploadDir = "uploads";
        java.io.File directory = new java.io.File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (file != null && !file.isEmpty()) {
            // 2. Dosyayı kaydet
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + java.io.File.separator + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 3. Veritabanına kaydet (Ön yüzün doğrudan erişebileceği URL formatı)
            shop.setImageUrl("/uploads/" + fileName);
        }

        shop.setName(shopName);
        shop.setPhoneNumber(phoneNumber);
        shopRepository.save(shop);

        // Dönüşte DTO kullandığın için shopService'in bunu desteklediğinden emin ol
        return ResponseEntity.ok(shopService.convertToDTO(shop));
    }
}