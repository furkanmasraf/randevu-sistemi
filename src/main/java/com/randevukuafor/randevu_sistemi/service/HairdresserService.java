package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.ServiceRepository;
import com.randevukuafor.randevu_sistemi.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;

@org.springframework.stereotype.Service
public class HairdresserService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ShopRepository shopRepository;

    // Yeni hizmet eklendiğinde, o dükkanın Redis'teki eski hizmet listesini siliyoruz
    // key = "#service.shop.id" diyerek sadece ilgili dükkanın cache'ini hedef alıyoruz!
    //@CacheEvict(value = "services", key = "#service.shop.id")
    public Service createService(Service service, Long shopId) {
        // 1. Veritabanından dükkanı bul
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));

        // 2. Gelen nesnenin dükkan bilgisini, veritabanından çektiğimiz gerçek dükkanla değiştir
        service.setShop(shop);

        // 3. Kaydet
        return serviceRepository.save(service);
    }

    // Belirli bir dükkanın tüm hizmetlerini getirme
    // key = "#shopId" sayesinde her dükkanın hizmet listesi Redis'te ayrı ayrı saklanır
    //@Cacheable(value = "services", key = "#shopId")
    public List<Service> getServicesByShop(Long shopId) {
        return serviceRepository.findByShopId(shopId);
    }
}