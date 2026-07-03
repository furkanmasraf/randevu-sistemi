package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.Appointment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a.appointmentTime FROM Appointment a WHERE a.employee.id = :empId AND FUNCTION('DATE', a.appointmentTime) = :date")
    List<LocalDateTime> findTakenSlotsByEmployeeAndDate(@Param("empId") Long empId, @Param("date") LocalDate date);

    // Müşterinin kendi randevularını listelemesi için
    List<Appointment> findByUserId(Long userId);

    // Dükkan sahibinin dükkana gelen randevuları görmesi için
    List<Appointment> findByShopId(Long shopId);

    // KRİTİK SORGU: Seçilen çalışan, seçilen saatte ve İPTAL EDİLMEMİŞ bir randevuya sahip mi?
    Optional<Appointment> findByEmployeeIdAndAppointmentTimeAndStatusNot(Long employeeId, LocalDateTime appointmentTime, String status);

    boolean existsByEmployeeIdAndAppointmentTimeAndStatusNot(Long employeeId, LocalDateTime appointmentTime, String status);
}