package com.randevukuafor.randevu_sistemi.service;

import com.randevukuafor.randevu_sistemi.model.Employee;
import com.randevukuafor.randevu_sistemi.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Yeni çalışan personeli veri tabanına kaydeder
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // Dükkana ait tüm çalışanları getirir
    public List<Employee> getEmployeesByShop(Long shopId) {
        return employeeRepository.findByShopId(shopId);
    }
}