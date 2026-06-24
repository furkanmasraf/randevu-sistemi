package com.randevukuafor.randevu_sistemi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Bu hata fırlatıldığında HTTP durum kodunun 400 (Bad Request) olacağını belirtiyoruz
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailAlreadyExistsException extends RuntimeException {

    // Sadece bir hata mesajı alan yapıcı metot (Constructor)
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}