package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.model.Appointment;
import com.randevukuafor.randevu_sistemi.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // 1. Randevu Oluşturma (POST http://localhost:8080/api/appointments)
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment savedAppointment = appointmentService.createAppointment(appointment);
        return ResponseEntity.ok(savedAppointment);
    }

    // 2. Müşteriye Göre Randevuları Listeleme (GET http://localhost:8080/api/appointments/user/{userId})
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByUser(@PathVariable Long userId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByUser(userId);
        return ResponseEntity.ok(appointments);
    }

    // 3. Dükkana Göre Randevuları Listeleme (GET http://localhost:8080/api/appointments/shop/{shopId})
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByShop(@PathVariable Long shopId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByShop(shopId);
        return ResponseEntity.ok(appointments);
    }
}