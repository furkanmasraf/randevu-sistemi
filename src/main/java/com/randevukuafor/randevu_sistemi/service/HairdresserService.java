package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;

@org.springframework.stereotype.Service
public class HairdresserService {

    @Autowired
    private ServiceRepository serviceRepository;

    // Yeni hizmet eklendiğinde, o dükkanın Redis'teki eski hizmet listesini siliyoruz
    // key = "#service.shop.id" diyerek sadece ilgili dükkanın cache'ini hedef alıyoruz!
    @CacheEvict(value = "services", key = "#service.shop.id")
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    // Belirli bir dükkanın tüm hizmetlerini getirme
    // key = "#shopId" sayesinde her dükkanın hizmet listesi Redis'te ayrı ayrı saklanır
    @Cacheable(value = "services", key = "#shopId")
    public List<Service> getServicesByShop(Long shopId) {
        return serviceRepository.findByShopId(shopId);
    }
}