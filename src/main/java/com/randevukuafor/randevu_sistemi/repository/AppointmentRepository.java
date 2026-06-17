package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Müşterinin kendi randevularını listelemesi için
    List<Appointment> findByUserId(Long userId);

    // Dükkan sahibinin dükkana gelen randevuları görmesi için
    List<Appointment> findByShopId(Long shopId);
}