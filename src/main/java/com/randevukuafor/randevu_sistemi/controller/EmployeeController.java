package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.EmployeeRepository;
import com.randevukuafor.randevu_sistemi.repository.ShopRepository;
import com.randevukuafor.randevu_sistemi.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ShopRepository shopRepository;
    private final EmployeeRepository employeeRepository;

    // 2. BURADA CONSTRUCTOR İLE ENJEKTE ET (Hatanın çözümü burasıdır)
    public EmployeeController(EmployeeService employeeService,
                              ShopRepository shopRepository,
                              EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.shopRepository = shopRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/shop/{shopId}")
    public ResponseEntity<?> addEmployeeToShop(@PathVariable Long shopId, @RequestBody Employee employee) {
        try {
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));

            employee.setShop(shop);

            System.out.println("DEBUG: Kaydedilmek üzere olan personel: " + employee.getFirstName());
            employeeRepository.save(employee);

            return ResponseEntity.ok(Map.of("message", "Personel başarıyla eklendi"));
        } catch (Exception e) {
            // HATA MESAJINI TAM OLARAK YAZDIR
            System.err.println("!!! PERSONEL KAYIT HATASI: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Hata: " + e.toString());
        }
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Employee>> getEmployeesByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(employeeService.getEmployeesByShop(shopId));
    }
}