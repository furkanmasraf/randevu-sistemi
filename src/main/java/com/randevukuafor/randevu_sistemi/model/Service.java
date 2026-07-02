package com.randevukuafor.randevu_sistemi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "services")
public class Service implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "duration_in_minutes", nullable = false)
    private Integer durationInMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @JsonBackReference
    private Shop shop;

    // Boş Yapıcı Metot (Hibernate için Şart)
    public Service() {
    }

    // Standart Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getDurationInMinutes() { return durationInMinutes; }
    public void setDurationInMinutes(Integer durationInMinutes) { this.durationInMinutes = durationInMinutes; }

    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
}