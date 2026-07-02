package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // 1. Çalışan Ekleme API'ı (POST http://localhost:8080/api/employees)
    @PostMapping("/shop/{shopId}")
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee, @PathVariable Long shopId) {
        try {
            // Hatanın kaynağını görmek için log ekliyoruz
            System.out.println("Gelen Employee: " + employee);
            return ResponseEntity.ok(employeeService.createEmployee(employee, shopId));
        } catch (Exception e) {
            e.printStackTrace(); // Hatanın asıl sebebini (NullPointerException vb.) IntelliJ konsolunda yazdırır
            return ResponseEntity.status(500).body("Hata: " + e.getMessage());
        }
    }

    // 2. Dükkanın Çalışanlarını Listeleme API'ı (GET http://localhost:8080/api/employees/shop/{shopId})
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Employee>> getEmployeesByShop(@PathVariable Long shopId) {
        List<Employee> employees = employeeService.getEmployeesByShop(shopId);
        return ResponseEntity.ok(employees);
    }
}