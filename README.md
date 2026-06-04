# 🇺🇦 DiiaClone — Java Edition

> Пародия на украинское государственное приложение «Дія»

---

## О проекте

**DiiaClone** — это учебный проект, который имитирует основную логику приложения «Дія»:  
хранение и управление цифровыми документами пользователя через личный кабинет.

📱 Приложение имитирует базовый функционал «Дії»: авторизацию пользователя,
просмотр цифровых документов и подачу заявок на государственные услуги.

---

## 🛠️ Технологии

Backend Java 17, Spring Boot 3
Авторизация Spring Security, JWT
База данныхPostgreSQL / H2 (для разработки)
ORM Spring Data JPA / Hibernate
Frontend HTML, CSS, JavaScript(Fetch API)
Сборка Maven

---

## 📡 API Endpoints

### 🔑 Авторизация
- `POST /api/auth/register` — регистрация пользователя  
- `POST /api/auth/login` — вход и получение JWT  

---

### 📄 Документы
- `GET /api/documents` — получить все документы пользователя  
- `GET /api/documents/passport` — получить паспорт  
- `GET /api/documents/inn` — получить ИНН  
- `POST /api/documents/add` — добавить новый документ  
