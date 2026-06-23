package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    // Çalışan ID'sine ve haftanın gününe göre aktif mesai kaydını getirir
    Optional<WorkingHours> findByEmployeeIdAndDayOfWeekAndIsActiveTrue(Long employeeId, int dayOfWeek);
}