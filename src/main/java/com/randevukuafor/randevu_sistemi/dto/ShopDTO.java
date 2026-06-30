package com.randevukuafor.randevu_sistemi.dto;

public class ShopDTO {
    private Long id;
    private String name;
    private String city;
    private String district;
    private String addressText;
    private double latitude;
    private double longitude;
    private boolean subscribed;
    private String startTime;
    private String endTime;

    // --- BOŞ VE PARAMETRELİ CONSTRUCTOR ---
    public ShopDTO() {}

    public ShopDTO(Long id, String name, String city, String district, String addressText,
                   double latitude, double longitude, boolean subscribed) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.district = district;
        this.addressText = addressText;
        this.latitude = latitude;
        this.longitude = longitude;
        this.subscribed = subscribed;
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

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public boolean isSubscribed() { return subscribed; }
    public void setSubscribed(boolean subscribed) { this.subscribed = subscribed; }

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
}