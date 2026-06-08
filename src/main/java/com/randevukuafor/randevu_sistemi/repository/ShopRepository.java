package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    // İleride şehir veya ilçeye göre kuaför aratmak için bu metotlar çok işimize yarayacak
    List<Shop> findByCity(String city);
    List<Shop> findByDistrict(String district);
}