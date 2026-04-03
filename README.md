# 🇺🇦 DiiaClone — пародия на украинское приложение «Дія» (Java Edition)
DiiaClone имитирует основную логику украинского госприложения «Дія» — хранение цифровых документов граждан с доступом через личный кабинет. В качестве фронтенда используется Telegram-бот, который общается с Java-бэкендом через REST API.

#Архитектура
🤖 Telegram Bot  →  Spring Boot REST API  →  PostgreSQL
   (фронтенд)           (бэкенд)             (база данных)

Авторизация через JWT-токены: бот получает токен при входе и передаёт его в каждом запросе к API.

#🗂️ Примерная структура эндпоинтов твоего API
POST /api/auth/register      — регистрация
POST /api/auth/login         — вход, получение JWT

GET  /api/documents          — все документы юзера
GET  /api/documents/passport — паспорт
GET  /api/documents/inn      — ИНН
POST /api/documents/add      — добавить документ
