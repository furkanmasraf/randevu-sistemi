package com.randevukuafor.randevu_sistemi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAppointmentRequest {

    @NotNull(message = "Müşteri ID boş bırakılamaz.")
    private Long userId;

    @NotNull(message = "Dükkan ID boş bırakılamaz.")
    private Long shopId;

    @NotNull(message = "Çalışan ID boş bırakılamaz.")
    private Long employeeId;

    @NotNull(message = "Hizmet ID boş bırakılamaz.")
    private Long serviceId;

    @NotNull(message = "Randevu zamanı seçilmelidir.")
    @Future(message = "Randevu tarihi geçmiş bir zaman olamaz.")
    private LocalDateTime appointmentTime;

    // Boş Constructor
    public CreateAppointmentRequest() {}

    // Getter ve Setter Metotları
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
}