package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.dto.AppointmentDTO;
import com.randevukuafor.randevu_sistemi.dto.CreateAppointmentRequest;
import com.randevukuafor.randevu_sistemi.model.*;
import com.randevukuafor.randevu_sistemi.repository.*;
import com.randevukuafor.randevu_sistemi.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) // React CORS engellerini aşmak için eklendi
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @GetMapping("/shop/employee-schedule")
    public ResponseEntity<List<String>> getEmployeeSchedule(
            @RequestParam Long employeeId,
            @RequestParam String date) { // Örnek format: "2026-07-05"

        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(appointmentService.getBusySlotsForEmployee(employeeId, localDate));
    }

    @GetMapping("/taken-slots")
    public ResponseEntity<List<String>> getTakenSlots(
            @RequestParam Long employeeId,
            @RequestParam String date) {

        LocalDate localDate = LocalDate.parse(date);
        List<LocalDateTime> takenSlots = appointmentRepository.findTakenSlotsByEmployeeAndDate(employeeId, localDate);

        // LocalDateTime listesini, saat formatında String listesine dönüştür
        List<String> timeStrings = takenSlots.stream()
                .map(dt -> dt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
                .toList();

        return ResponseEntity.ok(timeStrings);
    }

    // Randevu Oluşturma
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> payload) {
        try {
            Object shopIdObj = payload.get("shopId") != null ? payload.get("shopId") : payload.get("id");
            if (shopIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Dükkan bilgisi (shopId) bulunamadı."));
            }
            Long shopId = Long.valueOf(shopIdObj.toString());

            Object employeeIdObj = payload.get("employeeId");
            if (employeeIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Personel bilgisi seçilmedi."));
            }
            Long employeeId = Long.valueOf(employeeIdObj.toString());

            Object serviceIdObj = payload.get("serviceId");
            if (serviceIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Hizmet bilgisi seçilmedi."));
            }
            Long serviceId = Long.valueOf(serviceIdObj.toString());

            Object userIdObj = payload.get("userId");
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Kullanıcı oturumu bulunamadı, lütfen giriş yapın."));
            }
            Long userId = Long.valueOf(userIdObj.toString());

            String timeStr = payload.get("appointmentTime").toString();
            LocalDateTime appointmentTime = LocalDateTime.parse(timeStr);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Personel bulunamadı"));
            Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Hizmet bulunamadı"));

            Appointment appointment = new Appointment();
            appointment.setUser(user);
            appointment.setShop(shop);
            appointment.setEmployee(employee);
            appointment.setService(service);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setStatus("PENDING");

            appointmentRepository.save(appointment);

            return ResponseEntity.ok(Map.of("message", "Randevunuz başarıyla oluşturuldu!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("message", "Randevu alınamadı: " + e.getMessage()));
        }
    }

    @GetMapping("/shop/{shopId}/filter")
    public ResponseEntity<List<AppointmentDTO>> getFilteredAppointments(
            @PathVariable Long shopId,
            @RequestParam String filter) {

        return ResponseEntity.ok(appointmentService.getFilteredAppointments(shopId, filter));
    }

    // Müşterinin kendi randevularını çekebilmesi için eklenen endpoint
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByUser(@PathVariable Long userId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByUser(userId);
        return ResponseEntity.ok(appointments);
    }

    // Dükkan sahibinin (BARBER) kendi userId'sine göre dükkanına gelen tüm randevu taleplerini listeler
    @GetMapping("/shop/owner/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByShopOwner(@PathVariable Long userId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByShopOwner(userId);
        return ResponseEntity.ok(appointments);
    }

    // Alternatif: Doğrudan dükkan ID'sine göre randevuları listeleme
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByShop(@PathVariable Long shopId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByShop(shopId);
        return ResponseEntity.ok(appointments);
    }

    // Müşterinin kendi randevusunu iptal etmesi (Zaman kurallı)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        AppointmentDTO cancelledAppointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(cancelledAppointment);
    }

    // Yönetim panelinden (BarberDashboard) gelen "APPROVED" veya "REJECTED" durum güncellemelerini işler
    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        AppointmentDTO updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(updatedAppointment);
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

    @PatchMapping("/{id}/status")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        String status = statusMap.get("status");
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
        return ResponseEntity.ok(Map.of("message", "Durum güncellendi"));
    }

    @PostMapping("/block")
    public ResponseEntity<?> blockSlot(@RequestBody Map<String, Object> payload) {
        try {
            Long employeeId = Long.valueOf(payload.get("employeeId").toString());
            LocalDateTime time = LocalDateTime.parse(payload.get("appointmentTime").toString());

            Employee employee = employeeRepository.findById(employeeId).orElseThrow();

            Appointment block = new Appointment();
            block.setEmployee(employee);
            block.setShop(employee.getShop());
            block.setAppointmentTime(time);
            block.setStatus("BLOCKED");

            // 1. Bir "Bloklama" kullanıcısı veya dükkan sahibi ID'si (Örn: 1L)
            User admin = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("Admin kullanıcı bulunamadı"));
            block.setUser(admin);

            // 2. Bir "Bloklama Hizmeti" (Eğer yoksa, veritabanına "Bloklama" adında 0 TL bir hizmet ekle)
            Service blockService = serviceRepository.findById(1L).orElseThrow(() -> new RuntimeException("Bloklama hizmeti bulunamadı"));
            block.setService(blockService);

            appointmentRepository.save(block);

            return ResponseEntity.ok(Map.of("message", "Saat başarıyla bloklandı."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/unblock")
    public ResponseEntity<?> unblockSlot(@RequestParam Long employeeId, @RequestParam String appointmentTime) {
        LocalDateTime time = LocalDateTime.parse(appointmentTime);
        // Veritabanından o saate ait, status'ü 'BLOCKED' olan kaydı bul ve sil
        appointmentRepository.deleteByEmployeeIdAndAppointmentTimeAndStatus(employeeId, time, "BLOCKED");
        return ResponseEntity.ok(Map.of("message", "Bloklama kaldırıldı."));
    }
}