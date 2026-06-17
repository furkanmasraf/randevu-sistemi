package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByShopId(Long shopId);
}