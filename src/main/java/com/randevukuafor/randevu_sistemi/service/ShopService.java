package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.dto.ShopDTO;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        List<String> imageList = new ArrayList<>();

        // Veritabanındaki "url1,url2,url3" yapısını parçalayıp listeye atıyoruz
        if (shop.getVitrinImageUrl() != null && !shop.getVitrinImageUrl().isEmpty()) {
            String[] urls = shop.getVitrinImageUrl().split(",");
            imageList = Arrays.asList(urls);
        }
        ShopDTO dto = new ShopDTO(
                shop.getId(),
                shop.getName(),
                shop.getCity(),
                shop.getDistrict(),
                shop.getAddressText(),
                shop.isSubscribed(),
                shop.getPhoneNumber(),
                shop.getImageUrl(),
                imageList
        );
        dto.setStartTime(shop.getStartTime());
        dto.setEndTime(shop.getEndTime());
        return dto;
    }
}