# 🇺🇦 DiiaClone — пародия на украинское приложение «Дія» (Java Edition)
DiiaClone имитирует основную логику украинского госприложения «Дія» — хранение цифровых документов граждан с доступом через личный кабинет. В качестве фронтенда используется Telegram-бот, который общается с Java-бэкендом через REST API.

Архитектура
🤖 Telegram Bot  →  Spring Boot REST API  →  PostgreSQL
   (фронтенд)           (бэкенд)             (база данных)

Авторизация через JWT-токены: бот получает токен при входе и передаёт его в каждом запросе к API.

API Контракты
Базовый URL: http://localhost:8080/api

Все запросы кроме /auth/* требуют заголовка: Authorization: Bearer <token>


🔐 Авторизация
POST /auth/register
json// Request
{
  "firstName": "Иван",
  "lastName": "Петренко",
  "email": "ivan@example.com",
  "password": "password123",
  "birthDate": "1990-06-15",
  "taxId": "1234567890"
}

// Response 201
{
  "id": 1,
  "email": "ivan@example.com",
  "firstName": "Иван",
  "lastName": "Петренко"
}
POST /auth/login
json// Request
{ "email": "ivan@example.com", "password": "password123" }

// Response 200
{ "token": "eyJhbGci...", "type": "Bearer", "expiresIn": 86400 }

👤 Пользователь
GET /users/me — получить профиль
json// Response 200
{
  "id": 1,
  "firstName": "Иван",
  "lastName": "Петренко",
  "email": "ivan@example.com",
  "birthDate": "1990-06-15",
  "taxId": "1234567890"
}

📄 Документы
GET /documents — все мои документы
json// Response 200
[
  {
    "id": 1,
    "type": "PASSPORT",
    "issuedAt": "2015-03-20",
    "expiresAt": "2025-03-20",
    "data": { "series": "АА", "number": "123456", "issuedBy": "1234" }
  }
]
GET /documents/{type} — конкретный документ
Возможные значения type: PASSPORT, TAX_ID, DRIVERS_LICENSE
POST /documents — добавить документ
json// Request
{
  "type": "DRIVERS_LICENSE",
  "issuedAt": "2018-09-10",
  "expiresAt": "2028-09-10",
  "data": { "number": "АВ123456", "categories": ["B"] }
}
// Response 201 — созданный объект документа
DELETE /documents/{id} — удалить документ → 204 No Content

Коды ошибок
КодОписание401Токен отсутствует или невалиден404Ресурс не найден409Конфликт (например, email уже занят)

База данных
Таблица users: id, first_name, last_name, email, password_hash, birth_date, tax_id, created_at
Таблица documents: id, user_id (FK), type, issued_at, expires_at, data (JSONB), created_at
