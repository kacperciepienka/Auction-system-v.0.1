# Auction System — REST API + Thymeleaf UI

System aukcji internetowych zrealizowany w architekturze warstwowej **Controller → Service → Repository → Model**, zgodnie z wymaganiami przedmiotu **"Tworzenie usług sieciowych REST"**.

Projekt łączy:
* **REST API** (zgodne z zasadami REST, dokumentowane Swaggerem) — wymóg punktów 3.1, 3.2, 3.3 zadania
* **Frontend Thymeleaf** (Bootstrap 5) — wymóg punktu 4 zadania

Oba interfejsy działają na tym samym backendzie — REST API i strony HTML współdzielą warstwę serwisową, repozytoria i model danych.

---

## 🚀 Technologie

* Java 21
* Spring Boot 4.0.6
* Spring Web MVC
* Spring Data JPA + Hibernate
* **Spring Thymeleaf** (renderowanie HTML)
* H2 Database (in-memory)
* MapStruct + Lombok
* Bootstrap 5 + Bootstrap Icons (przez CDN)
* Swagger / OpenAPI
* Maven

---

## ✨ Funkcjonalności

### Frontend (Thymeleaf)
* 🏷️ Lista aukcji z filtrami (tytuł, kategoria, cena min/maks), sortowaniem i paginacją
* 📄 Szczegóły aukcji + historia ofert
* 💰 Składanie ofert (z walidacją po stronie serwera)
* ➕ Wystawianie nowych aukcji
* 🔐 Rejestracja, logowanie, wylogowanie (sesja w `HttpSession`)
* 👤 Profil użytkownika — zmiana username/email, lista własnych aukcji i ofert
* 🛡️ Panel administratora — zarządzanie użytkownikami, aukcjami i ofertami

### Backend (REST API)
* `POST /api/v0.1/users/register`, `/login`, `PUT`, `DELETE`, `GET`
* `POST /api/v0.1/auctions`, `GET`, `PUT`, `DELETE` (z filtrowaniem po kategorii, cenie, dacie, statusie, właścicielu)
* `POST /api/v0.1/bids/auction/{ref}` — składanie ofert
* `GET /api/v0.1/bids?...` — filtrowanie ofert
* Wersje **/admin/...** z dodatkowymi możliwościami
* Globalna obsługa wyjątków (`@RestControllerAdvice`)
* Scheduler automatycznie kończący wygasłe aukcje
* DTO, walidacja `@Valid`, kody HTTP 200/201/204/400/404/409/422

---

## 🏗️ Architektura

```
pl.auction_system/
├── controller/         ← REST API (@RestController)
├── web/                ← UI Thymeleaf (@Controller)
├── service/            ← Logika biznesowa (wspólna dla REST i UI)
├── repository/         ← Spring Data JPA
├── model/              ← Encje JPA (User, Auction, Bid, ...)
├── dto/                ← Request/Response DTO
├── mapper/             ← MapStruct
├── exception/          ← @RestControllerAdvice + custom exceptions
├── scheduler/          ← AuctionCloseScheduler
└── config/             ← DataSeeder
```

---

## ▶️ Uruchomienie

### Wymagania
* JDK 21
* Maven 3.9+ (lub użyj `./mvnw` z projektu)

### Start
```bash
./mvnw spring-boot:run
```

lub w IntelliJ IDEA: prawy klik na `AuctionSystemApplication` → **Run**.

Aplikacja dostępna pod:

| URL | Opis |
|-----|------|
| http://localhost:8080/ | Strona główna (Thymeleaf UI) |
| http://localhost:8080/swagger-ui/index.html | Dokumentacja REST API (Swagger) |
| http://localhost:8080/h2-console | Konsola bazy danych H2 (JDBC: `jdbc:h2:mem:auctionSystem`) |

---

## 👤 Dane przykładowe (DataSeeder)

Przy pierwszym starcie tworzone są automatycznie:

| Username | Email | Typ konta |
|----------|-------|-----------|
| `Admin1` | admin@aukcje.pl | **ADMIN** |
| `Kacper99` | kacper@example.com | USER |
| `Anna2024` | anna@example.com | USER |
| `Marek77` | marek@example.com | USER |

Plus **7 przykładowych aukcji** w różnych kategoriach (ELECTRONICS, ART, CAR, FASHION, HOME, BEAUTY).

Aby zalogować się — wystarczy wpisać nazwę użytkownika na stronie `/login` (system działa bez haseł, autoryzacja przez `HttpSession`).

---

## 📋 Zgodność z wymaganiami zadania

| Wymaganie | Status |
|-----------|--------|
| 3.1 REST endpointy użytkowników (POST/PUT/DELETE/GET) | ✅ |
| 3.2 REST endpointy aukcji + filtrowanie | ✅ |
| 3.3 REST endpoint składania ofert | ✅ |
| 4 Interfejs użytkownika (Thymeleaf) | ✅ |
| 5 DTO, walidacja, obsługa wyjątków, Swagger | ✅ |
| 6.1 Paginacja i filtrowanie | ✅ |
| 6.2 Sortowanie | ✅ |
| 6.3 Logowanie operacji (`@Slf4j`) | ✅ |

---

## 🗄️ H2 Console

URL: `http://localhost:8080/h2-console`
```
JDBC URL:  jdbc:h2:mem:auctionSystem
User:      sa
Password:  (puste)
```
