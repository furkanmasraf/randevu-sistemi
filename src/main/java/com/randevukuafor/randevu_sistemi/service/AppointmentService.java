package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.model.Appointment;
import com.randevukuafor.randevu_sistemi.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Randevu oluşturma
    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // Müşterinin randevularını getir
    public List<Appointment> getAppointmentsByUser(Long userId) {
        return appointmentRepository.findByUserId(userId);
    }

    // Dükkanın randevularını getir
    public List<Appointment> getAppointmentsByShop(Long shopId) {
        return appointmentRepository.findByShopId(shopId);
    }
}