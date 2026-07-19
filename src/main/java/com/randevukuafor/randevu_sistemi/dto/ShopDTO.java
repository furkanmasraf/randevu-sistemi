package com.randevukuafor.randevu_sistemi.dto;

import java.util.List;

public class ShopDTO {
    private Long id;
    private String name;
    private String city;
    private String district;
    private String addressText;
    private boolean subscribed;
    private String startTime;
    private String endTime;
    private String phoneNumber;
    private String imageUrl;
    private List<String> vitrinImageUrls;
    private String category;

    // --- BOŞ VE PARAMETRELİ CONSTRUCTOR ---
    public ShopDTO() {}

    public ShopDTO(Long id, String name, String city, String district, String addressText, boolean subscribed, String phoneNumber, String imageUrl, List<String> vitrinImageUrls, String category){
        this.id = id;
        this.name = name;
        this.city = city;
        this.district = district;
        this.addressText = addressText;
        this.subscribed = subscribed;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.vitrinImageUrls = vitrinImageUrls;
        this.category = category;
    }

    // --- GETTER VE SETTER METOTLARI ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddressText() { return addressText; }
    public void setAddressText(String addressText) { this.addressText = addressText; }


    public boolean isSubscribed() { return subscribed; }
    public void setSubscribed(boolean subscribed) { this.subscribed = subscribed; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getVitrinImageUrls() {
        return vitrinImageUrls;
    }
    public void setVitrinImageUrls(List<String> vitrinImageUrls) {
        this.vitrinImageUrls = vitrinImageUrls;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}