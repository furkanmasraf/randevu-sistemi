package com.randevukuafor.randevu_sistemi.security;

import com.randevukuafor.randevu_sistemi.model.User;
import com.randevukuafor.randevu_sistemi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Veritabanında kullanıcıyı e-posta ile bul
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        // Spring Security'nin tanıdığı formata dönüştür
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("{bcrypt}" + user.getPassword()) //Bu şifre artık düz metin değil, BCrypt ile hashlenmiş bir şifredir, bunu passwordEncoder ile tekrar hash'lemeye çalışma, doğrudan karşılaştır
                .roles(user.getRole().name())
                .build();
    }
}