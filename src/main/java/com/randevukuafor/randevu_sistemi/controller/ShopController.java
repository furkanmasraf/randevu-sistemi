package com.randevukuafor.randevu_sistemi.controller;

import com.randevukuafor.randevu_sistemi.dto.ShopDTO;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.service.ShopService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping("/register")
    public Shop registerShop(@RequestBody Shop shop) {
        return shopService.createShop(shop);
    }

    // Artık dış dünyaya Entity değil, güvenli DTO listesi dönüyor
    @GetMapping
    public List<ShopDTO> getAllShops() {
        return shopService.getAllShops();
    }

    // Filtreleme uç noktası da DTO yapısına geçirildi
    @GetMapping("/filter")
    public List<ShopDTO> getShopsByCity(@RequestParam String city) {
        return shopService.getShopsByCity(city);
    }
}