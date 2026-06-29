package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.dto.ShopDTO;
import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.EmployeeRepository;
import com.randevukuafor.randevu_sistemi.repository.ServiceRepository;
import com.randevukuafor.randevu_sistemi.service.ShopService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ShopController {

    private final ShopService shopService;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;

    // Tüm bağımlılıklar Constructor Injection ile güvenli bir şekilde enjekte edildi
    public ShopController(ShopService shopService,
                          EmployeeRepository employeeRepository,
                          ServiceRepository serviceRepository) {
        this.shopService = shopService;
        this.employeeRepository = employeeRepository;
        this.serviceRepository = serviceRepository;
    }

    @PostMapping("/register")
    public Shop registerShop(@RequestBody Shop shop) {
        return shopService.createShop(shop);
    }

    // Artık dış dünyaya Entity değil, güvenli DTO listesi dönüyor
    @GetMapping
    public List<ShopDTO> getAllShops() {
        return shopService.getAllShops();
    }

    // Filtreleme uç noktası da DTO yapısına geçirildi
    @GetMapping("/filter")
    public List<ShopDTO> getShopsByCity(@RequestParam String city) {
        return shopService.getShopsByCity(city);
    }

    // Ön yüzün (BookAppointment.tsx) dükkan ID'sine göre çalışanları çekebilmesi için eklenen endpoint
    @GetMapping("/{shopId}/employees")
    public List<Employee> getShopEmployees(@PathVariable Long shopId) {
        return employeeRepository.findByShopId(shopId);
    }

    // Ön yüzün (BookAppointment.tsx) dükkan ID'sine göre hizmetleri çekebilmesi için eklenen endpoint
    @GetMapping("/{shopId}/services")
    public List<Service> getShopServices(@PathVariable Long shopId) {
        return serviceRepository.findByShopId(shopId);
    }
}