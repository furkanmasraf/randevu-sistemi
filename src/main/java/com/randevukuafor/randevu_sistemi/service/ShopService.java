package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    // Yeni dükkan kaydetme
    public Shop createShop(Shop shop) {
        // İleride buraya "Bu kullanıcı gerçekten SHOP_OWNER mı?" gibi kurumsal kontroller ekleyebiliriz
        return shopRepository.save(shop);
    }

    // Tüm dükkanları listeleme
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    // Şehre göre dükkan getirme (Müşteriler arama yaparken kullanacak)
    public List<Shop> getShopsByCity(String city) {
        return shopRepository.findByCity(city);
    }
}