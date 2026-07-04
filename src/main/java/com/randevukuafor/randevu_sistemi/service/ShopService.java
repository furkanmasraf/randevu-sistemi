package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.ShopDTO;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Shop createShop(Shop shop) {
        return shopRepository.save(shop);
    }

    // Dönüş tipi List<ShopDTO> olarak güncellendi
    public List<ShopDTO> getAllShops() {
        return shopRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Dönüş tipi List<ShopDTO> olarak güncellendi
    public List<ShopDTO> getShopsByCity(String city) {
        return shopRepository.findByCity(city)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ShopDTO getShopById(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı!"));
        return convertToDTO(shop);
    }

    //  Entity -> DTO Dönüştürücü Yardımcı Metot (Mapper)
    public ShopDTO convertToDTO(Shop shop) {
        return new ShopDTO(
                shop.getId(),
                shop.getName(),
                shop.getCity(),
                shop.getDistrict(),
                shop.getAddressText(),
                shop.getLatitude(),
                shop.getLongitude(),
                shop.isSubscribed(),
                shop.getPhoneNumber(),
                shop.getImageUrl()
        );
    }
}