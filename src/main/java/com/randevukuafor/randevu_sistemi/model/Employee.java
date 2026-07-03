package com.randevukuafor.randevu_sistemi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.ToString;

@Entity
@Table(name = "employees")
@ToString(exclude = "shop")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Shop shop;

    // 1. Boş Yapıcı Metot (Hibernate ve Jackson dönüşümleri için ŞART)
    public Employee() {
    }

    // 2. Dolu Yapıcı Metot (İleride test yazarken işini kolaylaştırır)
    public Employee(String firstName, String lastName, Shop shop) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.shop = shop;
    }

    // 3. Standart Getter ve Setter Metotları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}