package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.service.ShopService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    // Dükkan Kayıt Etme (POST)
    @PostMapping("/register")
    public Shop registerShop(@RequestBody Shop shop) {
        return shopService.createShop(shop);
    }

    // Tüm Dükkanları Listeleme (GET)
    @GetMapping
    public List<Shop> getAllShops() {
        return shopService.getAllShops();
    }

    // Şehre göre filtreleme (GET) - Örn: /api/shops/filter?city=İstanbul
    @GetMapping("/filter")
    public List<Shop> getShopsByCity(@RequestParam String city) {
        return shopService.getShopsByCity(city);
    }
}