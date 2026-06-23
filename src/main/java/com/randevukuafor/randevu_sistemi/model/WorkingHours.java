package com.randevukuafor.randevu_sistemi.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "working_hours")
public class WorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee; // Bu mesai saati hangi çalışana ait?

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek; // 1 = Pazartesi, 2 = Salı ..., 7 = Pazar

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // Mesai başlangıcı (Örn: 09:00)

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // Mesai bitişi (Örn: 19:00)

    @Column(name = "is_active", nullable = false)
    private boolean isActive; // O gün çalışıyor mu? (Pazar günleri false yapılabilir)

    // Boilerplate (Boş Constructor, Getter & Setter)
    public WorkingHours() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}