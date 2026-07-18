# MakasLab — Uçtan Uca Randevu ve İşletme Yönetim Platformu

**MakasLab**, kuaför ve güzellik salonları için geliştirilmiş, işletme sahiplerinin randevu, personel ve hizmet süreçlerini tek bir yerden yönetebildiği; müşterilerin ise hızlı ve kolay bir şekilde randevu alabildiği modern bir **Full-Stack randevu yönetim platformudur.**

---

## Proje Hakkında

MakasLab, iki temel kullanıcı grubunun ihtiyaçlarına odaklanarak salon operasyonlarını dijitalleştirir:

**İşletme Sahipleri**
- Dükkan görsellerini ve profil bilgilerini yönetme
- Yeni hizmet ve personel ekleme
- Gelen randevu taleplerini onaylama / reddetme
- Personel çalışma takvimini (müsaitlik ve bloklu saatler) yönetme

**Müşteriler**
- Bölgedeki dükkanları listeleme ve inceleme
- İstenen hizmeti seçerek online randevu alma
- Geçmiş randevuları görüntüleme ve takip etme

---

## Teknik Mimari

### Backend — Java Spring Boot

| Alan | Teknoloji / Yaklaşım |
|---|---|
| Mimari | Modüler ve genişletilebilir katmanlı yapı |
| Güvenlik | Spring Security + JWT ile rol tabanlı kimlik doğrulama (Dükkan Sahibi, Çalışan, Müşteri) |
| Veritabanı | PostgreSQL + Spring Data JPA ile optimize edilmiş ilişkisel veri yönetimi |
| Hata Yönetimi | `@RestControllerAdvice` ile standartlaştırılmış JSON hata çıktıları (`ErrorResponse`) |
| Hata Toleransı | Resilience4j entegrasyonu |

### Frontend — React & TypeScript

| Alan | Teknoloji / Yaklaşım |
|---|---|
| Arayüz | Modern, sade ve "premium" hissi veren kullanıcı odaklı tasarım |
| Responsive | Mobil ve masaüstü için optimize edilmiş dinamik yapı |
| State Yönetimi | `useState`, `useEffect` ve Context API ile gerçek zamanlı veri akışı |
| İletişim | Axios tabanlı güvenli API servis katmanı |

---

## Proje Klasör Yapısı

### Backend
```
src/main/java/com/randevukuafor/randevu_sistemi/
├── config/           # Güvenlik ve JWT konfigürasyonları
├── controller/       # REST API endpoint'leri
├── dto/              # Veri transfer objeleri
├── entity/           # Veritabanı modelleri
├── repository/       # Veritabanı erişim katmanı
└── service/          # İş mantığı (business logic)
```

### Frontend
```
src/
├── pages/            # BarberDashboard, CustomerDashboard, Home, Login vb.
├── services/         # Axios API servisleri
└── components/       # UI bileşenleri
```

---

## Neden MakasLab?

- **Modern UI** — Kullanıcıyı yormayan, şık ve profesyonel arayüz
- **Güvenli** — JWT tabanlı yetkilendirme ile yüksek güvenlik standardı
- **Ölçeklenebilir** — Modüler yapı sayesinde e-posta bildirimleri, ödeme entegrasyonu gibi yeni özellikler kolayca eklenebilir
- **Dayanıklı** — Merkezi hata yönetimi ve hata tolerans mekanizmalarıyla kararlı çalışma

---

## Kurulum

### Backend

1. `src/main/resources/application.properties` dosyasında veritabanı bağlantı ayarlarını yapılandırın.
2. Maven ile bağımlılıkları yükleyin:
   ```bash
   mvn install
   ```
3. Uygulamayı başlatın:
   ```bash
   mvn spring-boot:run
   ```

### Frontend

1. Proje dizinine gidin:
   ```bash
   cd frontend
   ```
2. Bağımlılıkları yükleyin:
   ```bash
   npm install
   ```
3. Geliştirme ortamını başlatın:
   ```bash
   npm run dev
   ```

---

## Geliştirici

**Furkan Masraf**
Full-Stack Developer