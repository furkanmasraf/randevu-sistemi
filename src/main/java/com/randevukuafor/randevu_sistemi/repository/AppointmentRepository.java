package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Müşterinin kendi randevularını listelemesi için
    List<Appointment> findByUserId(Long userId);

    // Dükkan sahibinin dükkana gelen randevuları görmesi için
    List<Appointment> findByShopId(Long shopId);

    // KRİTİK SORGU: Seçilen çalışan, seçilen saatte ve İPTAL EDİLMEMİŞ bir randevuya sahip mi?
    Optional<Appointment> findByEmployeeIdAndAppointmentTimeAndStatusNot(Long employeeId, LocalDateTime appointmentTime, String status);
}