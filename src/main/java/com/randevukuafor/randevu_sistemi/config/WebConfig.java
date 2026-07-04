package com.randevukuafor.randevu_sistemi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "uploads/" klasöründeki dosyaları "/uploads/**" URL'si üzerinden tarayıcıya açar
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}