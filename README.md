
# 🎟️ Smart Event Platform

> Piattaforma full-stack per la gestione di eventi, registrazioni, notifiche e interazioni in tempo reale.  
> Basata su **Spring Boot + MongoDB**, pensata per scalabilità, performance e sicurezza.

---

## 📌 Descrizione del progetto

Smart Event Platform è una web application RESTful progettata per:
- Creare, cercare e gestire eventi pubblici o privati
- Consentire agli utenti di registrarsi agli eventi con gestione di lista d’attesa
- Inviare notifiche (in real-time o persistenti)
- Gestire l’accesso utente con **JWT**, ruoli e permessi
- Esporre funzionalità in modo sicuro, performante e scalabile

---

## 🧰 Stack Tecnologico

| Tecnologia | Descrizione |
|------------|-------------|
| Spring Boot | Backend REST API |
| MongoDB | Database documentale NoSQL |
| Spring Data Mongo | Data layer |
| Spring Security + JWT | Autenticazione e autorizzazione |
| WebSocket (STOMP) | Notifiche in real-time |
| Redis (opzionale) | Caching |
| Docker | Deployment containerizzato |
| Swagger / OpenAPI | Documentazione API |
| Spring Boot Actuator | Monitoring e metrics |

---

## 🧠 Principali funzionalità

- ✅ CRUD utenti con registrazione e login sicuri (JWT)
- ✅ CRUD eventi, con limiti posti e privacy
- ✅ Registrazioni agli eventi con gestione lista d’attesa
- ✅ Notifiche automatiche e in tempo reale (WebSocket)
- ✅ Sistema ruoli: `USER`, `ORGANIZER`, `ADMIN`
- ✅ Validazione dati e gestione errori centralizzata
- ✅ Caching intelligente (eventi popolari, eventi utente)
- ✅ Operazioni programmate (task scheduler)
- ✅ Monitoraggio con Actuator e metriche custom
- ✅ Dockerizzazione completa del progetto

---

## 📁 Struttura del progetto

```
com.smartplatform
├── config            # Configurazioni di sicurezza, Mongo, WebSocket
├── controller        # RestController per API pubbliche e protette
├── dto               # Oggetti di scambio dati (request/response)
├── exception         # Gestione centralizzata errori
├── model             # Modelli MongoDB (@Document)
├── repository        # Interfacce MongoRepository
├── security          # Filtri JWT, configurazione Spring Security
├── service
│   └── impl          # Business logic separata in interfacce/implementazioni
├── util              # Utility varie (mapper, helper)
└── SmartPlatformApplication.java
```

---

## 📆 Roadmap tecnica (milestone)

### 🔹 Fase 1 - Setup & Modello Base
- [x] Creazione struttura progetto
- [x] Setup MongoDB + Docker
- [x] Modelli base (`User`, `Event`, `Registration`, `Notification`)
- [x] DTO mapping & validazione

### 🔹 Fase 2 - CRUD & Sicurezza
- [x] CRUD Utente, Eventi
- [x] Login / Signup con JWT + ruoli
- [x] Middleware di protezione per le rotte

### 🔹 Fase 3 - Registrazioni e Waitlist
- [x] Registrazione evento con gestione posti
- [x] Lista d’attesa e promozione automatica
- [x] Notifiche di cambio stato

### 🔹 Fase 4 - Notifiche & WebSocket
- [ ] Implementazione WebSocket (STOMP)
- [ ] Notifiche persistenti e real-time

### 🔹 Fase 5 - Ottimizzazione & Cache
- [ ] Integrazione Redis per caching eventi
- [ ] Eviction cache su modifica

### 🔹 Fase 6 - Task Scheduler & Metrics
- [ ] Cleanup eventi scaduti
- [ ] Promemoria automatici
- [ ] Integrazione Spring Boot Actuator

### 🔹 Fase 7 - Docker & Deployment
- [ ] Dockerfile + Docker Compose
- [ ] Profilazione ambienti (dev/prod)
- [ ] Pronto per deployment su cloud

---

## ⚙️ Requisiti minimi

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MongoDB 6+
- Redis (opzionale per cache)

---

## ▶️ Avvio locale (dev)

```bash
# Clona il repo
git clone https://github.com/tuo-username/smart-event-platform.git
cd smart-event-platform

# Avvia MongoDB e Redis con Docker Compose
docker-compose up -d

# Avvia l'app Spring Boot
./mvnw spring-boot:run
```

---

## 📌 API Principali

| Metodo | Endpoint | Autenticazione | Descrizione |
|--------|----------|----------------|-------------|
| `POST` | `/auth/register` | ❌ | Registrazione utente |
| `POST` | `/auth/login` | ❌ | Login utente (JWT) |
| `GET`  | `/events` | ❌ | Lista eventi pubblici |
| `POST` | `/events/{id}/register` | ✅ | Iscrizione evento |
| `GET`  | `/notifications` | ✅ | Notifiche utente |
| `GET`  | `/actuator/health` | ❌ | Stato dell'applicazione |

---

## 🔐 Sicurezza

- Autenticazione JWT
- Filtri personalizzati per `Bearer token`
- Controlli su ruoli e permessi con `@PreAuthorize`

---

## 🚧 TODO / miglioramenti futuri

- [ ] Modulo amministrazione (moderazione eventi)
- [ ] Supporto allegati per eventi (es. immagine copertina)
- [ ] API pubbliche documentate via Swagger UI
- [ ] Versionamento API (`/api/v1`)

---

## 👨‍💻 Autore

Gabbo Junior – Java backend developer  
Contatti, GitHub e contributi nel file `CONTRIBUTORS.md` ✨

---
