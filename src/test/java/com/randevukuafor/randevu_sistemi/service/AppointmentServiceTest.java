package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.AppointmentDTO;
import com.randevukuafor.randevu_sistemi.dto.CreateAppointmentRequest;
import com.randevukuafor.randevu_sistemi.model.*;
import com.randevukuafor.randevu_sistemi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test; // DOĞRU: JUnit 5 Jupiter import edildi
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*; // DOĞRU: Mockito.never() buradan gelecek

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private CreateAppointmentRequest validRequest;
    private LocalDateTime appointmentTime;

    @BeforeEach
    void setUp() {
        appointmentTime = LocalDateTime.of(2026, 7, 20, 14, 0);

        validRequest = new CreateAppointmentRequest();
        validRequest.setUserId(1L);
        validRequest.setShopId(1L);
        validRequest.setEmployeeId(1L);
        validRequest.setServiceId(1L);
        validRequest.setAppointmentTime(appointmentTime);
    }

    @Test
    @DisplayName("Senaryo 1: Saat boşsa randevu başarıyla oluşturulmalıdır")
    public void shouldCreateAppointment_WhenSlotIsAvailable() {
        // Given
        when(appointmentRepository.findByEmployeeIdAndAppointmentTimeAndStatusNot(1L, appointmentTime, "CANCELLED"))
                .thenReturn(Optional.empty());

        User user = new User(); user.setFirstName("Ahmet"); user.setLastName("Yılmaz");
        Shop shop = new Shop(); shop.setName("Furkan Erkek Kuaförü");
        Employee employee = new Employee(); employee.setFirstName("Mehmet"); employee.setLastName("Usta");
        com.randevukuafor.randevu_sistemi.model.Service service = new com.randevukuafor.randevu_sistemi.model.Service();
        service.setName("Saç Kesimi & Yıkama");
        service.setPrice(BigDecimal.valueOf(500.00));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        Appointment savedAppointment = new Appointment();
        savedAppointment.setId(4L);
        savedAppointment.setUser(user);
        savedAppointment.setShop(shop);
        savedAppointment.setEmployee(employee);
        savedAppointment.setService(service);
        savedAppointment.setAppointmentTime(appointmentTime);
        savedAppointment.setStatus("PENDING");

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // When
        AppointmentDTO result = appointmentService.createAppointment(validRequest);

        // Then
        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("PENDING", result.getStatus());
        assertEquals("Ahmet Yılmaz", result.getCustomerName());

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Senaryo 2: Çalışan o saatte doluysa IllegalArgumentException fırlatmalıdır")
    public void shouldThrowException_WhenSlotIsConflicting() {
        // Given
        // 1. Validasyonların patlamadan geçmesi için TÜM ilişkili entity'lerin var olduğunu simüle ediyoruz
        User user = new User();
        Shop shop = new Shop();
        Employee employee = new Employee();
        com.randevukuafor.randevu_sistemi.model.Service service = new com.randevukuafor.randevu_sistemi.model.Service();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        // 2. Çakışan bir randevu nesnesi simüle ediyoruz (Saat dolu!)
        Appointment conflictingAppointment = new Appointment();
        when(appointmentRepository.findByEmployeeIdAndAppointmentTimeAndStatusNot(1L, appointmentTime, "CANCELLED"))
                .thenReturn(Optional.of(conflictingAppointment));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(validRequest);
        });

        assertEquals("Seçilen çalışan bu saatte doludur! Lütfen başka bir saat veya çalışan seçiniz.", exception.getMessage());

        // Çakışma olduğu için save metodunun ASLA çağrılmadığını doğrula
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}