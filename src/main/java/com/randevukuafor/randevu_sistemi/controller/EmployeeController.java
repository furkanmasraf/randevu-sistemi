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
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    // 2. Dükkanın Çalışanlarını Listeleme API'ı (GET http://localhost:8080/api/employees/shop/{shopId})
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Employee>> getEmployeesByShop(@PathVariable Long shopId) {
        List<Employee> employees = employeeService.getEmployeesByShop(shopId);
        return ResponseEntity.ok(employees);
    }
}