package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.AppointmentDTO;
import com.randevukuafor.randevu_sistemi.dto.CreateAppointmentRequest;
import com.randevukuafor.randevu_sistemi.model.*;
import com.randevukuafor.randevu_sistemi.repository.*;
import com.randevukuafor.randevu_sistemi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private WorkingHoursRepository workingHoursRepository;

    // Girdi: Request, Çıktı: DTO
    public AppointmentDTO createAppointment(CreateAppointmentRequest request) {

        // 1. Varlık Kontrolleri
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri bulunamadı! ID: " + request.getUserId()));

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Dükkan bulunamadı! ID: " + request.getShopId()));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Çalışan bulunamadı! ID: " + request.getEmployeeId()));

        com.randevukuafor.randevu_sistemi.model.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Hizmet bulunamadı! ID: " + request.getServiceId()));

        //  MESAİ VE ÇALIŞMA SAATLERİ KONTROLÜ
        int requestDayOfWeek = request.getAppointmentTime().getDayOfWeek().getValue(); // 1 (Pazartesi) - 7 (Pazar)
        LocalTime requestTime = request.getAppointmentTime().toLocalTime();

        // Çalışanın o güne ait aktif bir mesai kaydı var mı?
        WorkingHours workingHours = workingHoursRepository
                .findByEmployeeIdAndDayOfWeekAndIsActiveTrue(request.getEmployeeId(), requestDayOfWeek)
                .orElseThrow(() -> new IllegalArgumentException("Seçilen çalışan bu gün hizmet vermemektedir!"));

        // Seçilen saat mesai saatleri dışındaysa kapıdan çevir
        if (requestTime.isBefore(workingHours.getStartTime()) || requestTime.isAfter(workingHours.getEndTime())) {
            throw new IllegalArgumentException("Seçilen saat çalışanın mesai saatleri dışındadır! Mesai: "
                    + workingHours.getStartTime() + " - " + workingHours.getEndTime());
        }

        // 2. ÇAKIŞMA KONTROLÜ (Kayıt işleminden önce kulis arkasında kontrol ediyoruz)
        Optional<Appointment> conflictingAppointment = appointmentRepository
                .findByEmployeeIdAndAppointmentTimeAndStatusNot(request.getEmployeeId(), request.getAppointmentTime(), "CANCELLED");

        if (conflictingAppointment.isPresent()) {
            throw new IllegalArgumentException("Seçilen çalışan bu saatte doludur! Lütfen başka bir saat veya çalışan seçiniz.");
        }

        // 3. İlişkileri Bağlama ve Nesne Oluşturma
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setShop(shop);
        appointment.setEmployee(employee);
        appointment.setService(service);
        appointment.setAppointmentTime(request.getAppointmentTime());

        // 4. Veritabanına Kaydetme
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 5. DTO Dönüşümü ve Yanıt Gönderme
        return convertToDTO(savedAppointment);
    }

    // iptal algoritması
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        // 1. Randevuyu bul, yoksa özel hatamızı fırlat
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Randevu bulunamadı! ID: " + appointmentId));

        LocalDateTime now = LocalDateTime.now();

        // 2. Geçmiş randevu kontrolü
        if (appointment.getAppointmentTime().isBefore(now)) {
            throw new IllegalArgumentException("Geçmiş tarihteki bir randevuyu iptal edemezsiniz.");
        }

        // 3. Zaman kontrolü (Şu an ile randevu saati arasındaki dakika farkı)
        long minutesBetween = Duration.between(now, appointment.getAppointmentTime()).toMinutes();

        // 2 saat = 120 dakika kuralı
        if (minutesBetween < 120) {
            throw new IllegalArgumentException("Randevunuza 2 saatten az zaman kaldığı için iptal işlemini gerçekleştiremezsiniz.");
        }

        // 4. Durumu güncelle ve kaydet
        appointment.setStatus("CANCELLED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // 5. Kurumsal çıktı için DTO'ya dönüştür
        return convertToDTO(updatedAppointment);
    }

    // Listeleme metotları
    public List<AppointmentDTO> getAppointmentsByUser(Long userId) {
        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByShop(Long shopId) {
        return appointmentRepository.findByShopId(shopId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Entity -> DTO Dönüşümünü yapan yardımcı metot (Manuel Mapping)
    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getUser().getFirstName() + " " + appointment.getUser().getLastName(),
                appointment.getShop().getName(),
                appointment.getEmployee().getFirstName() + " " + appointment.getEmployee().getLastName(),
                appointment.getService().getName(),
                appointment.getService().getPrice(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}