package com.randevukuafor.randevu_sistemi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RandevuSistemiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RandevuSistemiApplication.class, args);
	}

}
