package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.model.Shop;
import com.randevukuafor.randevu_sistemi.repository.EmployeeRepository;
import com.randevukuafor.randevu_sistemi.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ShopRepository shopRepository;

    // Yeni çalışan personeli veri tabanına kaydeder
    public Employee createEmployee(Employee employee, Long shopId) {
        // Dükkan ID'sini burada manuel olarak zorla set et, gelen nesnenin içindeki shop'u önemseme
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));

        employee.setShop(shop); // Gelen her neyse, veritabanından çektiğimiz shop ile değiştiriyoruz
        return employeeRepository.save(employee);
    }

    // Dükkana ait tüm çalışanları getirir
    public List<Employee> getEmployeesByShop(Long shopId) {
        return employeeRepository.findByShopId(shopId);
    }
}