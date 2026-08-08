package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByCity(String city);
    List<Shop> findByDistrict(String district);
    Optional<Shop> findByOwnerId(Long ownerId);
    List<Shop> findAllByOwnerId(Long ownerId);
    List<Shop> findByCategory(String category);
}