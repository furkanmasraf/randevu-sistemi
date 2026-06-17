package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.model.Service;
import com.randevukuafor.randevu_sistemi.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@org.springframework.stereotype.Service
public class HairdresserService {

    @Autowired
    private ServiceRepository serviceRepository;

    // Yeni hizmet ekleme
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    // Belirli bir dükkanın tüm hizmetlerini getirme
    public List<Service> getServicesByShop(Long shopId) {
        return serviceRepository.findByShopId(shopId);
    }
}