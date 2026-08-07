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
import com.randevukuafor.randevu_sistemi.repository.ShopRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/owner/{userId}")
    public ResponseEntity<?> getShopByOwner(@PathVariable Long userId) {
        Optional<Shop> shopOpt = shopRepository.findByOwnerId(userId);

        if (shopOpt.isEmpty()) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User owner = userOpt.get();
                Shop newShop = new Shop();
                newShop.setName((owner.getFirstName() != null ? owner.getFirstName() : "Salon") + " " + (owner.getLastName() != null ? owner.getLastName() : "") + " Kuaför");
                newShop.setCity("İstanbul");
                newShop.setDistrict("Beyoğlu");
                newShop.setAddressText("Adres henüz belirtilmedi.");
                newShop.setCategory("Erkek Kuaförü");
                newShop.setOwner(owner);
                Shop savedShop = shopRepository.save(newShop);
                shopOpt = Optional.of(savedShop);
            } else {
                return ResponseEntity.status(404).body("Kullanıcı ve Dükkan bulunamadı.");
            }
        }

        Shop shop = shopOpt.get();

        ShopDTO dto = new ShopDTO();
        dto.setId(shop.getId());
        dto.setName(shop.getName());
        dto.setAddressText(shop.getAddressText());
        dto.setCity(shop.getCity());
        dto.setDistrict(shop.getDistrict());
        dto.setCategory(shop.getCategory());
        dto.setStartTime(shop.getStartTime());
        dto.setEndTime(shop.getEndTime());
        dto.setPhoneNumber(shop.getPhoneNumber());
        dto.setImageUrl(shop.getImageUrl());
        if (shop.getVitrinImageUrl() != null && !shop.getVitrinImageUrl().isEmpty()) {
            dto.setVitrinImageUrls(Arrays.asList(shop.getVitrinImageUrl().split(",")));
        } else {
            dto.setVitrinImageUrls(new ArrayList<>());
        }

        return ResponseEntity.ok(dto);
    }

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

    @PostMapping("/{shopId}/services")
    public ResponseEntity<?> addServiceToShop(@PathVariable Long shopId, @RequestBody Service service) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));

        service.setShop(shop);
        Service savedService = serviceRepository.save(service);

        return ResponseEntity.ok(Map.of("message", "Hizmet başarıyla eklendi", "id", savedService.getId()));
    }

    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Hizmet bulunamadı"));

        serviceRepository.delete(service);
        return ResponseEntity.ok(Map.of("message", "Hizmet başarıyla silindi"));
    }

    @GetMapping("/{shopId}/details")
    public ResponseEntity<ShopDTO> getShopDetails(@PathVariable Long shopId) {
        return ResponseEntity.ok(shopService.getShopById(shopId));
    }

    // İŞLETME SAHİBİNİN TÜM BİLGİLERİNİ (AD, ŞEHİR, İLÇE, ADRES, KATEGORİ, TELEFON, LOGO, VİTRİN) GÜNCELLEME ENDPOINT'İ
    @PutMapping(value = "/{shopId}/update-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShopDTO> updateShopWithImage(
            @PathVariable Long shopId,
            @RequestParam(value = "existingImageUrls", required = false) String existingImageUrlsJson,
            @RequestParam(value = "vitrinFiles", required = false) List<MultipartFile> vitrinFiles,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "shopName", required = false) String shopName,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "addressText", required = false) String addressText,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "logoDeleted", required = false, defaultValue = "false") boolean logoDeleted
    ) throws IOException {

        Optional<Shop> shopOpt = shopRepository.findById(shopId);
        if (shopOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Shop shop = shopOpt.get();

        // 1. LOGO İŞLEMLERİ
        if (logo != null && !logo.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(logo.getBytes(), ObjectUtils.emptyMap());
                shop.setImageUrl(uploadResult.get("secure_url").toString());
            } catch (Exception e) {
                System.err.println("Cloudinary logo yükleme hatası: " + e.getMessage());
            }
        } else if (logoDeleted) {
            shop.setImageUrl(null);
        }

        // 2. Vitrin Görsel İşlemleri (Mevcut ve Yeni)
        List<String> remainingUrls = new ArrayList<>();
        if (existingImageUrlsJson != null && !existingImageUrlsJson.equals("[]") && !existingImageUrlsJson.isBlank()) {
            String cleanJson = existingImageUrlsJson.replace("[", "").replace("]", "").replace("\"", "");
            if (!cleanJson.isEmpty()) {
                remainingUrls.addAll(Arrays.asList(cleanJson.split(",")));
            }
        }

        if (vitrinFiles != null) {
            for (MultipartFile file : vitrinFiles) {
                if (file != null && !file.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                        remainingUrls.add(uploadResult.get("secure_url").toString());
                    } catch (Exception e) {
                        System.err.println("Cloudinary vitrin görseli yükleme hatası: " + e.getMessage());
                    }
                }
            }
        }

        shop.setVitrinImageUrl(String.join(",", remainingUrls));
        
        // İşletme sahibinin girdiği tüm metin alanlarını güncelle
        if (shopName != null && !shopName.isBlank()) {
            shop.setName(shopName);
        }
        if (phoneNumber != null) {
            shop.setPhoneNumber(phoneNumber);
        }
        if (city != null && !city.isBlank()) {
            shop.setCity(city);
        }
        if (district != null && !district.isBlank()) {
            shop.setDistrict(district);
        }
        if (addressText != null && !addressText.isBlank()) {
            shop.setAddressText(addressText);
        }
        if (category != null && !category.isBlank()) {
            shop.setCategory(category);
        }

        shopRepository.save(shop);

        return ResponseEntity.ok(shopService.convertToDTO(shop));
    }

    @GetMapping("/category/{category}")
    public List<ShopDTO> getShopsByCategory(@PathVariable String category) {
        return shopRepository.findByCategory(category)
                .stream()
                .map(shopService::convertToDTO)
                .collect(Collectors.toList());
    }
}