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

    // --- TEKİL VE GÜVENLİ DÜKKAN KAYIT METODU (MÜKERRER DÜKKAN OLUŞMASINI ENGELLER) ---
    @PostMapping("/register")
    public ResponseEntity<?> registerShop(@RequestBody Map<String, Object> payload) {
        if (payload == null || payload.get("ownerId") == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ownerId parametresi gereklidir."));
        }

        Long ownerId = Long.valueOf(payload.get("ownerId").toString());
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Kullanıcının mevcut dükkanı var mı kontrol et (Tekil Dükkan Mantığı)
        List<Shop> existingShops = shopRepository.findAllByOwnerId(ownerId);
        Shop shop;
        if (!existingShops.isEmpty()) {
            // Var olan dükkanı kullan ve güncelle
            shop = existingShops.get(existingShops.size() - 1);
            // Eğer geçmişte mükerrer 2. dükkan oluşturulmuşsa fazlalıkları temizle
            if (existingShops.size() > 1) {
                for (int i = 0; i < existingShops.size() - 1; i++) {
                    try { shopRepository.delete(existingShops.get(i)); } catch (Exception ignored) {}
                }
            }
        } else {
            shop = new Shop();
            shop.setOwner(owner);
        }

        Object nameObj = payload.get("name") != null ? payload.get("name") : payload.get("shopName");
        if (nameObj != null && !nameObj.toString().isBlank()) {
            shop.setName(nameObj.toString());
        }

        Object cityObj = payload.get("city");
        if (cityObj != null && !cityObj.toString().isBlank()) {
            shop.setCity(cityObj.toString());
        }

        Object distObj = payload.get("district");
        if (distObj != null && !distObj.toString().isBlank()) {
            shop.setDistrict(distObj.toString());
        }

        Object addrObj = payload.get("addressText");
        if (addrObj != null && !addrObj.toString().isBlank()) {
            shop.setAddressText(addrObj.toString());
        }

        Object catObj = payload.get("category");
        if (catObj != null && !catObj.toString().isBlank()) {
            shop.setCategory(catObj.toString());
        }

        Object phoneObj = payload.get("phoneNumber");
        if (phoneObj != null && !phoneObj.toString().isBlank()) {
            shop.setPhoneNumber(phoneObj.toString());
        } else if (shop.getPhoneNumber() == null && owner.getPhoneNumber() != null) {
            shop.setPhoneNumber(owner.getPhoneNumber());
        }

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

    // --- KULLANICIYA AİT DÜKKANI İŞLETME SAHİBİNİN GİRDİĞİ GERÇEK BİLGİLERLE GETİR ---
    @GetMapping("/owner/{userId}")
    public ResponseEntity<?> getShopByOwner(@PathVariable Long userId) {
        List<Shop> shops = shopRepository.findAllByOwnerId(userId);

        Shop shop;
        if (shops.isEmpty()) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User owner = userOpt.get();
                Shop newShop = new Shop();
                String defaultName = (owner.getFirstName() != null ? owner.getFirstName() : "") + " " + (owner.getLastName() != null ? owner.getLastName() : "");
                defaultName = defaultName.trim().isEmpty() ? "Salon Kuaför" : defaultName.trim() + " Salonu";
                newShop.setName(defaultName);
                newShop.setPhoneNumber(owner.getPhoneNumber() != null ? owner.getPhoneNumber() : "");
                newShop.setCity("İstanbul");
                newShop.setDistrict("Beyoğlu");
                newShop.setAddressText("İstiklal Cad. No:78");
                newShop.setCategory("Erkek Kuaförü");
                newShop.setOwner(owner);
                shop = shopRepository.save(newShop);
            } else {
                return ResponseEntity.status(404).body("Kullanıcı ve Dükkan bulunamadı.");
            }
        } else {
            // Eğer veritabanında 1'den fazla mükerrer dükkan oluşmuşsa kullanıcının en son oluşturduğu gerçek dükkanı al
            // ve eski taslak mükerrer kaydı veritabanından temizle
            shop = shops.get(shops.size() - 1);
            if (shops.size() > 1) {
                for (int i = 0; i < shops.size() - 1; i++) {
                    try { shopRepository.delete(shops.get(i)); } catch (Exception ignored) {}
                }
            }
        }

        ShopDTO dto = new ShopDTO();
        dto.setId(shop.getId());
        dto.setName(shop.getName() != null && !shop.getName().isBlank() ? shop.getName() : shop.getOwner().getFirstName() + " Salonu");
        dto.setAddressText(shop.getAddressText() != null ? shop.getAddressText() : "");
        dto.setCity(shop.getCity() != null ? shop.getCity() : "");
        dto.setDistrict(shop.getDistrict() != null ? shop.getDistrict() : "");
        dto.setCategory(shop.getCategory() != null ? shop.getCategory() : "Erkek Kuaförü");
        dto.setStartTime(shop.getStartTime());
        dto.setEndTime(shop.getEndTime());
        dto.setPhoneNumber(shop.getPhoneNumber() != null ? shop.getPhoneNumber() : (shop.getOwner().getPhoneNumber() != null ? shop.getOwner().getPhoneNumber() : ""));
        dto.setImageUrl(shop.getImageUrl() != null ? shop.getImageUrl() : "");
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