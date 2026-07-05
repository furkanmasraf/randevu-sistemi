package com.randevukuafor.randevu_sistemi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AppointmentDTO {
    private Long id;
    private String customerName;
    private String shopName;
    private String employeeName;
    private String serviceName;
    private BigDecimal price;
    private LocalDateTime appointmentTime;
    private String status;
    private String customerPhone;


    // Kolay eşleme için Dolu Constructor
    public AppointmentDTO(Long id, String customerName, String shopName, String employeeName, String serviceName, BigDecimal price, LocalDateTime appointmentTime, String status, String customerPhone) {
        this.id = id;
        this.customerName = customerName;
        this.shopName = shopName;
        this.employeeName = employeeName;
        this.serviceName = serviceName;
        this.price = price;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.customerPhone = customerPhone;
    }

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String status) { this.customerPhone = customerPhone; }
}