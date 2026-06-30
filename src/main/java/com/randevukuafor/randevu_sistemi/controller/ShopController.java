package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.dto.ShopDTO;
import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.EmployeeRepository;
import com.randevukuafor.randevu_sistemi.repository.ServiceRepository;
import com.randevukuafor.randevu_sistemi.service.ShopService;
import com.randevukuafor.randevu_sistemi.repository.ShopRepository; // Dükkan kayıt işlemleri için eklendi
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ShopDTO> getShopByOwnerId(@PathVariable Long ownerId) {
        Shop shop = shopRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Bu kullanıcıya ait dükkan bulunamadı"));

        // Entity verilerini temiz bir şekilde DTO'ya aktarıyoruz
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(shop.getId());
        shopDTO.setName(shop.getName());
        shopDTO.setCity(shop.getCity());
        shopDTO.setDistrict(shop.getDistrict());
        shopDTO.setAddressText(shop.getAddressText());
        shopDTO.setStartTime(shop.getStartTime());
        shopDTO.setEndTime(shop.getEndTime());

        return ResponseEntity.ok(shopDTO);
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
    public ResponseEntity<Map<String, Object>> addServiceToShop(
            @PathVariable Long shopId,
            @RequestBody Service service) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));

        service.setShop(shop);
        Service savedService = serviceRepository.save(service);

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedService.getId());
        response.put("name", savedService.getName());
        response.put("price", savedService.getPrice());
        response.put("durationInMinutes", savedService.getDurationInMinutes());

        return ResponseEntity.ok(response);
    }

    // Hizmet Silme Endpoint'i
    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Hizmet bulunamadı"));

        serviceRepository.delete(service);
        return ResponseEntity.ok(Map.of("message", "Hizmet başarıyla silindi"));
    }
}