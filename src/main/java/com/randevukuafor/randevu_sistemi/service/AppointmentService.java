package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.AppointmentDTO;
import com.randevukuafor.randevu_sistemi.dto.CreateAppointmentRequest;
import com.randevukuafor.randevu_sistemi.model.*;
import com.randevukuafor.randevu_sistemi.repository.*;
import com.randevukuafor.randevu_sistemi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
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

    // Girdi: Request, Çıktı: DTO
    public AppointmentDTO createAppointment(CreateAppointmentRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri bulunamadı! ID: " + request.getUserId()));

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Dükkan bulunamadı! ID: " + request.getShopId()));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Çalışan bulunamadı! ID: " + request.getEmployeeId()));

        com.randevukuafor.randevu_sistemi.model.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Hizmet bulunamadı! ID: " + request.getServiceId()));

        // Entity nesnemizi oluşturup veritabanı ilişkilerini bağlıyoruz
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setShop(shop);
        appointment.setEmployee(employee);
        appointment.setService(service);
        appointment.setAppointmentTime(request.getAppointmentTime());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Veritabanına kaydolduktan sonra bunu temiz bir DTO'ya dönüştürüp dönüyoruz
        return convertToDTO(savedAppointment);
    }

    // Listeleme metotlarını da DTO listesi dönecek şekilde güncelliyoruz
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