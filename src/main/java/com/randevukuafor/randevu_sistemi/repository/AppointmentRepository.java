package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.Appointment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.employee.id = :employeeId AND FUNCTION('DATE', a.appointmentTime) = :date AND a.status IN ('APPROVED', 'PENDING', 'BLOCKED')")
    List<Appointment> findAvailableAppointments(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.employee.id = :employeeId AND FUNCTION('DATE', a.appointmentTime) = :date AND a.status IN ('APPROVED', 'PENDING')")
    List<Appointment> findByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    // AppointmentRepository.java
    @Query("SELECT a.appointmentTime FROM Appointment a WHERE a.employee.id = :employeeId AND DATE(a.appointmentTime) = :date AND a.status != 'CANCELLED'")
    List<LocalDateTime> findTakenSlotsByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    @Modifying
    @Transactional
    void deleteByEmployeeIdAndAppointmentTimeAndStatus(Long employeeId, LocalDateTime appointmentTime, String status);

    // Müşterinin kendi randevularını listelemesi için
    List<Appointment> findByUserId(Long userId);

    // Dükkan sahibinin dükkana gelen randevuları görmesi için
    List<Appointment> findByShopId(Long shopId);

    // KRİTİK SORGU: Seçilen çalışan, seçilen saatte ve İPTAL EDİLMEMİŞ bir randevuya sahip mi?
    Optional<Appointment> findByEmployeeIdAndAppointmentTimeAndStatusNot(Long employeeId, LocalDateTime appointmentTime, String status);

    boolean existsByEmployeeIdAndAppointmentTimeAndStatusNot(Long employeeId, LocalDateTime appointmentTime, String status);

    // Sadece aktif (onaylı veya bekleyen) randevuları kontrol eder
    boolean existsByEmployeeIdAndAppointmentTimeAndStatusIn(Long employeeId, LocalDateTime appointmentTime, List<String> statuses);
}