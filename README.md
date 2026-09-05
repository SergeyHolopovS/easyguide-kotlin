# EasyGuide Backend

Backend платформы бронирования экскурсий у частных гидов.

## Стек

- **Kotlin** + **Spring Boot 4.1.1** (Web MVC, Security, Validation, Scheduling)
- **PostgreSQL** — основное хранилище, миграции через **Flyway**
- **Spring Data JPA / Hibernate** — доступ к данным
- **JWT** (JJWT) — аутентификация, `BCrypt` — хеширование паролей
- **springdoc-openapi** — автогенерация OpenAPI/Swagger
- **Testcontainers** (PostgreSQL) + JUnit 5 + **ArchUnit** — тесты, в т.ч. архитектурные
- **Docker / Docker Compose** — упаковка и локальный/прод запуск

## Архитектура: слои и правило зависимостей

Проект разбит на фичи (`user`, `tour`, `tourslot`, `booking`, `review`) плюс сквозной модуль
`shared`. Внутри каждой фичи — четыре слоя:

```
presentation   → application → domain
infrastructure ───────────────↗
```

- **`domain`** (`domain.model`, `domain.repository`, `domain.exception`) — бизнес-модель и
  правила. **Не зависит ни от чего**: ни от Spring, ни от JPA/Jackson, ни от других слоёв.
  `domain.repository` — только интерфейсы (порты), реализация им не принадлежит.
- **`application`** (`application.usecase`, `application.dto`, `application.query`,
  `application.port`) — сценарии использования (use-case на сценарий, метод `execute`) и
  доменные порты (`Clock`, `PasswordHasher`, `TokenIssuer`, `FileStorage`, query-порты для
  чтения). Зависит только от `domain`. **Не зависит** от `infrastructure` и `presentation`.
- **`infrastructure`** (`infrastructure.persistence`, `infrastructure.security`, ...) —
  реализация портов: JPA-сущности и мапперы, адаптеры репозиториев, JWT/BCrypt, файловое
  хранилище, планировщик. Зависит от `domain` и `application`, свободно использует Spring/JPA.
- **`presentation`** (контроллеры + DTO) — HTTP-слой. Зовёт только `application` (use-case'ы,
  `Result`/`View`-модели), не лезет в `domain.model` напрямую (кроме enum'ов — они используются
  как типы `@RequestParam` для фильтров, это осознанное исключение).

Правило зависимостей и часть перечисленных ограничений (repository — только интерфейсы,
use-case — `*UseCase` с единственным `execute`, контроллеры не тянут `domain.model`) проверяются
автоматически в `ArchitectureTest` (`./gradlew test --tests ArchitectureTest`).

Кросс-агрегатные читающие сценарии (каталог туров, календарь слотов, брони, публичный профиль
гида) реализованы как отдельные query-порты (`*Query`) с JPQL-проекциями прямо в view-модели,
минуя доменные объекты — классический CQRS для read-side.

## Как запустить

### Локально (dev)

```bash
docker compose up --build
```

Поднимает Postgres и приложение одной командой. API — на `http://localhost:8080`.

Чтобы сразу получить тестовые данные (гиды, туры, брони, отзывы — см. ниже), добавьте профиль:

```bash
SPRING_PROFILES_ACTIVE=local docker compose up --build
```

### Прод (self-hosted VPS)

1. Скопируйте `.env.prod.example` → `.env.prod`, заполните реальные `POSTGRES_PASSWORD`,
   `APP_JWT_SECRET` (32+ случайных байта, `openssl rand -base64 48`), `APP_CORS_ALLOWED_ORIGIN`,
   `DOMAIN`, `ACME_EMAIL`. **Дефолтов для секретов в `docker-compose.prod.yml` нет** — без
   переменной команда упадёт с явной ошибкой, а не тихо уйдёт на дев-значение.
2. Направьте DNS-запись домена на IP сервера.
3. `docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build`

HTTPS обеспечивает `caddy` — автоматически получает и продлевает сертификат Let's Encrypt для
`DOMAIN`, порт БД наружу не публикуется.

### Прод (Railway / Render)

Обе платформы умеют собирать образ прямо из `Dockerfile` и сами терминируют HTTPS — сервис
`caddy` не нужен. Нужно только:
- добавить Postgres как managed-аддон;
- прописать переменные окружения `SPRING_DATASOURCE_URL/USERNAME/PASSWORD`, `APP_JWT_SECRET`,
  `APP_CORS_ALLOWED_ORIGIN` в настройках сервиса;
- приложение само слушает порт из `PORT` (см. `server.port=${PORT:8080}` в
  `application.properties`), Railway/Render это ожидают.

> **Важно:** я не выполнял реальный деплой — для этого нужны ваш аккаунт/домен/биллинг, а это
> необратимое действие с внешней инфраструктурой, которое не делается без явного запроса. Всё
> выше — подготовленная конфигурация и инструкция для самостоятельного запуска.
>
> **Ссылка на живой стенд:** _(заполнить после деплоя)_.

## API

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Тестовые данные (профиль `local`)

При запуске с активным Spring-профилем `local` (`SPRING_PROFILES_ACTIVE=local` или
`--spring.profiles.active=local`) автоматически загружаются тестовые данные —
`LocalDataSeeder` (`shared/infrastructure/seed`). Сидер идемпотентен: при повторном
запуске проверяет наличие первого гида по email и, если данные уже загружены, ничего
не делает.

**Пароль для всех сидовых аккаунтов:** `Passw0rd!2026`

### Гиды
| Имя | Email | Город |
|---|---|---|
| Анна Иванова | guide1@easyguide.local | Москва |
| Дмитрий Соколов | guide2@easyguide.local | Санкт-Петербург |
| Мария Петрова | guide3@easyguide.local | Казань |

### Путешественники
| Имя | Email |
|---|---|
| Иван Смирнов | traveler1@easyguide.local |
| Ольга Кузнецова | traveler2@easyguide.local |

### Остальные данные
- 10 опубликованных туров (по 3–4 на гида), 3 города, разные категории, у каждого тура заполненное описание и 1–2 фото.
- У каждого тура — 6 будущих слотов (следующие ~30 дней) для бронирования.
- 10 бронирований: по одной в статусах `PENDING`, `CONFIRMED`, `REJECTED`, `CANCELLED`, и 6 в статусе `COMPLETED` (на отдельных прошедших слотах).
- 5 отзывов — на 5 из 6 завершённых броней. Одна `COMPLETED`-бронь **намеренно оставлена без отзыва** (для проверки сценария «оставить отзыв»).
