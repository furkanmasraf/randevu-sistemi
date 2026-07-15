package com.randevukuafor.randevu_sistemi.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    // Eğer değişken bulunamazsa "placeholder" değerini kullanır ve uygulamanın çökmesini önler
    @Value("${cloudinary.cloud-name:default_name}")
    private String cloudName;

    @Value("${cloudinary.api-key:default_key}")
    private String apiKey;

    @Value("${cloudinary.api-secret:default_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }
}