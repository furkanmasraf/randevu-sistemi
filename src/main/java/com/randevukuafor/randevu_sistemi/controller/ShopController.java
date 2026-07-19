package com.randevukuafor.randevu_sistemi.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.randevukuafor.randevu_sistemi.dto.ShopDTO;
import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.repository.EmployeeRepository;
import com.randevukuafor.randevu_sistemi.repository.ServiceRepository;
import com.randevukuafor.randevu_sistemi.repository.UserRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ShopController {

    private final Cloudinary cloudinary;
    private final ShopService shopService;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    // Tüm bağımlılıklar tek bir Constructor Injection ile güvenli şekilde enjekte edildi
    public ShopController(Cloudinary cloudinary,
                          ShopService shopService,
                          EmployeeRepository employeeRepository,
                          ServiceRepository serviceRepository,
                          ShopRepository shopRepository,
                          UserRepository userRepository) {
        this.cloudinary = cloudinary;
        this.shopService = shopService;
        this.employeeRepository = employeeRepository;
        this.serviceRepository = serviceRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerShop(@RequestBody Map<String, Object> payload) {
        Long ownerId = Long.valueOf(payload.get("ownerId").toString());
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Shop shop = new Shop();
        shop.setName(payload.get("name").toString());
        shop.setCity(payload.get("city").toString());
        shop.setDistrict(payload.get("district").toString());
        shop.setAddressText(payload.get("addressText").toString());
        if (payload.containsKey("category")) {
            shop.setCategory(payload.get("category").toString());
        }
        shop.setOwner(owner);

        Shop savedShop = shopRepository.save(shop);
        return ResponseEntity.ok(savedShop);
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
        dto.setPhoneNumber(shop.getPhoneNumber());
        dto.setImageUrl(shop.getImageUrl());
        if (shop.getVitrinImageUrl() != null) {
            dto.setVitrinImageUrls(Arrays.asList(shop.getVitrinImageUrl().split(",")));
        } else {
            dto.setVitrinImageUrls(new ArrayList<>());
        }

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
            @RequestParam(value = "existingImageUrls") String existingImageUrlsJson,
            @RequestParam(value = "vitrinFiles", required = false) List<MultipartFile> vitrinFiles,
            @RequestParam(value = "logo", required = false) MultipartFile logo, // Logo parametresini ekledik
            @RequestParam("shopName") String shopName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "logoDeleted", required = false) boolean logoDeleted // Parametreye isim verdik
    ) throws IOException {

        Shop shop = shopRepository.findById(shopId).orElseThrow();

        // 1. LOGO İŞLEMLERİ
        if (logo != null && !logo.isEmpty()) {
            // Yeni logo yüklendiyse Cloudinary'e yükle
            Map uploadResult = cloudinary.uploader().upload(logo.getBytes(), ObjectUtils.emptyMap());
            shop.setImageUrl(uploadResult.get("secure_url").toString());
        } else if (logoDeleted) {
            // Eğer frontend'den logoDeleted = true geldiyse
            shop.setImageUrl(null);
        }

        // 2. Vitrin Görsel İşlemleri (Mevcut ve Yeni)
        List<String> remainingUrls = new ArrayList<>();
        if (existingImageUrlsJson != null && !existingImageUrlsJson.equals("[]")) {
            String cleanJson = existingImageUrlsJson.replace("[", "").replace("]", "").replace("\"", "");
            if (!cleanJson.isEmpty()) {
                remainingUrls.addAll(Arrays.asList(cleanJson.split(",")));
            }
        }

        if (vitrinFiles != null) {
            for (MultipartFile file : vitrinFiles) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                remainingUrls.add(uploadResult.get("secure_url").toString());
            }
        }

        shop.setVitrinImageUrl(String.join(",", remainingUrls));
        shop.setName(shopName);
        shop.setPhoneNumber(phoneNumber);
        shopRepository.save(shop);

        return ResponseEntity.ok(shopService.convertToDTO(shop));
    }

    @GetMapping("/category/{category}")
    public List<ShopDTO> getShopsByCategory(@PathVariable String category) {
        // ShopRepository'den dönen List<Shop>'u DTO'ya dönüştürüyoruz
        return shopRepository.findByCategory(category)
                .stream()
                .map(shopService::convertToDTO)
                .collect(Collectors.toList());
    }
}