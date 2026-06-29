package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.dto.AppointmentDTO;
import com.randevukuafor.randevu_sistemi.dto.CreateAppointmentRequest;
import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.repository.EmployeeRepository;
import com.randevukuafor.randevu_sistemi.repository.ServiceRepository;
import com.randevukuafor.randevu_sistemi.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) // 🚀 React CORS engellerini aşmak için eklendi
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Ön yüzün dükkan sayfasında personelleri listelemesi için gerekli repository'ler (Eğer ilgili servislerin varsa oraya da taşıyabilirsin)
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    // @Valid anotasyonu sayesinde DTO üzerindeki kurallar (Not-Null, Future) kapıda denetlenir
    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentDTO savedAppointment = appointmentService.createAppointment(request);
        return ResponseEntity.ok(savedAppointment);
    }

    // Ön yüzün (BookAppointment.tsx) dükkan ID'sine göre çalışanları çekebilmesi için eklenen endpoint
    @GetMapping("/shop/{shopId}/employees")
    public ResponseEntity<List<Employee>> getShopEmployees(@PathVariable Long shopId) {
        return ResponseEntity.ok(employeeRepository.findByShopId(shopId));
    }

    // Ön yüzün (BookAppointment.tsx) dükkan ID'sine göre hizmetleri çekebilmesi için eklenen endpoint
    @GetMapping("/shop/{shopId}/services")
    public ResponseEntity<List<Service>> getShopServices(@PathVariable Long shopId) {
        return ResponseEntity.ok(serviceRepository.findByShopId(shopId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByUser(@PathVariable Long userId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByUser(userId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByShop(@PathVariable Long shopId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByShop(shopId);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        AppointmentDTO cancelledAppointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(cancelledAppointment);
    }
}