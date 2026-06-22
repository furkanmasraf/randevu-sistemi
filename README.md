# ✂️ Kuaför ve Çalışan Randevu Sistemi (Backend API)

Bu proje, bir kuaför salonunun dükkan kayıtlarını, çalışan yönetimini ve müşterilerin randevu akışlarını yönetmek için geliştirilmiş, kurumsal standartlara uygun **Java Spring Boot** tabanlı bir RESTful API projesidir.

Proje mimarisi; güvenlik, ölçeklenebilirlik, hata toleransı (fault tolerance) ve veri tutarlılığı ön planda tutularak tasarlanmıştır.

---

## 🚀 Öne Çıkan Özellikler & Teknik Altyapı

*   **Java Spring Boot & Maven:** Güçlü, modüler ve genişletilebilir backend mimarisi.
*   **JWT (JSON Web Token) Authentication:** Spring Security ile entegre edilmiş, rol tabanlı (Dükkan Sahibi, Çalışan, Müşteri) güvenli kimlik doğrulama altyapısı.
*   **PostgreSQL & Spring Data JPA:** İlişkisel veritabanı modellemesi (Shop - Employee - User ilişkileri) ve optimize edilmiş SQL sorguları.
*   **Dockerization:** Veritabanı ve çevre birimlerinin izole bir şekilde, tek komutla ayağa kaldırılabilmesi için Docker konfigürasyonu.
*   **Resilience4j:** Sistemin hata toleransını artırmak, mikroservis veya dış bağımlılık akışlarında çökmeleri önlemek amacıyla devre kesici (Circuit Breaker) entegrasyonu.
*   **Merkezi Hata Yönetimi:** `@RestControllerAdvice` ve `@ExceptionHandler` kullanılarak, istemciye (Postman/Frontend) standart ve anlamlı JSON hata çıktıları (`ErrorResponse`) dönen kurumsal yapı.

---

## 📂 Proje Klasör Yapısı

```text
src/main/java/com/randevukuafor/randevu_sistemi/
│
├── config/           # Spring Security, JWT ve Genel Konfigürasyonlar
├── controller/       # REST API Endpoint'leri (Auth, Shop, Employee vb.)
├── dto/              # Veri Transfer Objeleri (Request / Response DTOs)
├── entity/           # PostgreSQL Veritabanı Modelleri
├── exception/        # Global Hata Yakalayıcı (GlobalExceptionHandler, ErrorResponse)
├── repository/       # Veritabanı Erişim Katmanı (Spring Data JPA)
└── service/          # İş Mantığı (Business Logic) Katmanı