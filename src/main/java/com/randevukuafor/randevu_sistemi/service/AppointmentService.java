package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.AppointmentDTO;
import com.randevukuafor.randevu_sistemi.dto.CreateAppointmentRequest;
import com.randevukuafor.randevu_sistemi.model.*;
import com.randevukuafor.randevu_sistemi.repository.*;
import com.randevukuafor.randevu_sistemi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
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

    public List<String> getBusySlotsForEmployee(Long employeeId, LocalDate date) {
        // Repository'de tanımladığımız metodu çağırıyoruz
        List<LocalDateTime> busyTimes = appointmentRepository.findTakenSlotsByEmployeeAndDate(employeeId, date);

        // Sadece saat bilgisini (HH:mm) döndürüyoruz
        return busyTimes.stream()
                .map(dt -> dt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());
    }

    //Geçmiş Bugün Gelecek Filtreleme!!
    public List<AppointmentDTO> getFilteredAppointments(Long shopId, String filter) {
        List<Appointment> all = appointmentRepository.findByShopId(shopId);
        LocalDateTime now = LocalDateTime.now();

        return all.stream()
                .filter(a -> {
                    if (filter.equals("past")) return a.getAppointmentTime().isBefore(now);
                    if (filter.equals("today")) return a.getAppointmentTime().toLocalDate().isEqual(now.toLocalDate());
                    if (filter.equals("future")) return a.getAppointmentTime().isAfter(now);
                    return true;
                })
                .map(appointment -> {
                    return new AppointmentDTO(
                            appointment.getId(),
                            appointment.getUser().getFirstName() + " " + appointment.getUser().getLastName(),
                            appointment.getShop().getName(),
                            appointment.getEmployee().getFirstName() + " " + appointment.getEmployee().getLastName(),
                            appointment.getService().getName(),
                            appointment.getService().getPrice(),
                            appointment.getAppointmentTime(),
                            appointment.getStatus(),
                            appointment.getUser().getPhoneNumber(),
                            appointment.getShop().getAddressText(),
                            appointment.getShop().getPhoneNumber()
                    );
                })
                .toList();
    }

    // 1. Randevu Kaydetme İş Mantığı
    public AppointmentDTO createAppointment(CreateAppointmentRequest request) {

        // Varlık Kontrolleri
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri bulunamadı! ID: " + request.getUserId()));

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Dükkan bulunamadı! ID: " + request.getShopId()));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Çalışan bulunamadı! ID: " + request.getEmployeeId()));

        com.randevukuafor.randevu_sistemi.model.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Hizmet bulunamadı! ID: " + request.getServiceId()));

        // MESAİ VE ÇALIŞMA SAATLERİ KONTROLÜ
        int requestDayOfWeek = request.getAppointmentTime().getDayOfWeek().getValue(); // 1 (Pazartesi) - 7 (Pazar)
        LocalTime requestTime = request.getAppointmentTime().toLocalTime();

        WorkingHours workingHours = workingHoursRepository
                .findByEmployeeIdAndDayOfWeekAndIsActiveTrue(request.getEmployeeId(), requestDayOfWeek)
                .orElseThrow(() -> new IllegalArgumentException("Seçilen çalışan bu gün hizmet vermemektedir!"));

        if (requestTime.isBefore(workingHours.getStartTime()) || requestTime.isAfter(workingHours.getEndTime())) {
            throw new IllegalArgumentException("Seçilen saat çalışanın mesai saatleri dışındadır! Mesai: "
                    + workingHours.getStartTime() + " - " + workingHours.getEndTime());
        }

        // ÇAKIŞMA KONTROLÜ
        Optional<Appointment> conflictingAppointment = appointmentRepository
                .findByEmployeeIdAndAppointmentTimeAndStatusNot(request.getEmployeeId(), request.getAppointmentTime(), "CANCELLED");

        if (conflictingAppointment.isPresent()) {
            throw new IllegalArgumentException("Seçilen çalışan bu saatte doludur! Lütfen başka bir saat veya çalışan seçiniz.");
        }

        // İlişkileri Bağlama ve Nesne Oluşturma
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setShop(shop);
        appointment.setEmployee(employee);
        appointment.setService(service);
        appointment.setAppointmentTime(request.getAppointmentTime());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(savedAppointment);
    }

    //  2. Müşteri Randevu İptal Algoritması
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Randevu bulunamadı! ID: " + appointmentId));

        LocalDateTime now = LocalDateTime.now();

        if (appointment.getAppointmentTime().isBefore(now)) {
            throw new IllegalArgumentException("Geçmiş tarihteki bir randevuyu iptal edemezsiniz.");
        }

        long minutesBetween = Duration.between(now, appointment.getAppointmentTime()).toMinutes();

        if (minutesBetween < 120) {
            throw new IllegalArgumentException("Randevunuza 2 saatten az zaman kaldığı için iptal işlemini gerçekleştiremezsiniz.");
        }

        appointment.setStatus("CANCELLED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(updatedAppointment);
    }

    //  3. Dükkan Sahibinin (Berber) Randevu Durumunu Güncellemesi (APPROVED / REJECTED)
    public AppointmentDTO updateAppointmentStatus(Long appointmentId, String newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Randevu bulunamadı! ID: " + appointmentId));

        appointment.setStatus(newStatus.toUpperCase());
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(updatedAppointment);
    }

    //  4. Listeleme Metotları
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

    //  5. Berberin Kullanıcı Kimliğine Göre Kendi Dükkanının Randevularını Filtreleme
    public List<AppointmentDTO> getAppointmentsByShopOwner(Long userId) {
        return appointmentRepository.findAll().stream()
                .filter(app -> app.getShop().getOwner() != null && app.getShop().getOwner().getId().equals(userId))
                .filter(app -> !"BLOCKED".equals(app.getStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //  6. Entity -> DTO Dönüşümünü yapan yardımcı metot (Manuel Mapping)
    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getUser().getFirstName() + " " + appointment.getUser().getLastName(),
                appointment.getShop().getName(),
                appointment.getEmployee().getFirstName() + " " + appointment.getEmployee().getLastName(),
                appointment.getService().getName(),
                appointment.getService().getPrice(),
                appointment.getAppointmentTime(),
                appointment.getStatus(),
                appointment.getUser().getPhoneNumber(),
                appointment.getShop().getAddressText(),
                appointment.getShop().getPhoneNumber()
        );
    }

    public void blockSlot(Long employeeId, LocalDateTime appointmentTime) {
        // 1. Zaten dolu mu kontrol et
        List<String> activeStatuses = Arrays.asList("APPROVED", "PENDING", "BLOCKED");
        if (appointmentRepository.existsByEmployeeIdAndAppointmentTimeAndStatusIn(employeeId, appointmentTime, activeStatuses)) {
            throw new IllegalStateException("Bu saat zaten dolu veya bloklanmış.");
        }

        // 2. Personeli bul
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Personel bulunamadı"));

        // 3. Bloklama kaydını oluştur
        Appointment block = new Appointment();
        block.setEmployee(employee);
        block.setAppointmentTime(appointmentTime);
        block.setStatus("BLOCKED");

        appointmentRepository.save(block);
    }
}