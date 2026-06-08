package com.randevukuafor.randevu_sistemi.repository;

import com.randevukuafor.randevu_sistemi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // İleride login işlemleri ve kayıt kontrolü için email ile kullanıcı bulmamız gerekecek
    Optional<User> findByEmail(String email);
}