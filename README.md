# Slotify — Concurrency-Safe Slot Booking System

> A high-concurrency slot booking backend designed to prevent double booking under race conditions using Optimistic Locking.

## 🚀 Key Highlights

- Concurrency-safe booking preventing double bookings using **Optimistic Locking** (`@Version`)
- Conflict detection with **HTTP 409** under simultaneous slot booking attempts
- **Idempotent** booking endpoints preventing duplicate requests from retries
- Slot locking with TTL expiration using **scheduled cleanup jobs**
- Eliminated **N+1 queries** using EntityGraph (JOIN FETCH optimization)
- Horizontally scalable architecture with **stateless JWT authentication**
- Production-ready with Flyway migrations, Docker deployment

## 🛠 Tech Stack

- **Backend**: Java 21, Spring Boot 3.2.4, Spring Security
- **Database**: PostgreSQL 15 (Production), H2 (Local)
- **ORM**: Spring Data JPA (Hibernate)
- **Migration**: Flyway
- **Authentication**: Stateless JSON Web Tokens (JWT)
- **Caching**: Spring Cache (In-Memory)
- **DevOps**: Docker, Docker Compose, GitHub Actions
- **API Documentation**: Swagger UI (OpenAPI 3.0)
- **Load Testing**: k6

## 🧠 Core Engineering Decision

### Optimistic Locking vs Pessimistic Locking

- Chose **Optimistic Locking** to maximize throughput under high read scenarios
- Avoided **Pessimistic Locking** to prevent thread blocking and database lock contention
- Conflict handled at application level using version mismatch → HTTP 409
- **Trade-off:** Higher retry rate under extreme contention, but no threads blocked
- **Benefit:** Better scalability, lower DB lock contention, no deadlock risk
- **Retry Strategy:** Clients retry on HTTP 409 with exponential backoff

## 🚀 Local Setup

### Option 1: Using Docker (Recommended)

```bash
docker-compose up --build
```

API available at `http://localhost:8080`, Swagger at `http://localhost:8080/swagger-ui.html`

### Option 2: Using Maven Locally

```bash
./mvnw spring-boot:run
```

Uses H2 in-memory database. Swagger at `http://localhost:8080/swagger-ui.html`

## 📓 API Endpoints

Interactive API docs: `http://localhost:8080/swagger-ui.html`

Postman collection: `postman_collection.json`

### Authentication Flow

1. POST `/auth/register` - `{"email": "test@test.com", "password": "pass"}`
2. POST `/auth/login` - Returns JWT token
3. Use `Authorization: Bearer <token>` for `/bookings/**` routes

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login, returns JWT |
| GET | `/events` | List events (paginated) |
| GET | `/events/{id}` | Get event details |
| POST | `/bookings/lock-seat` | Lock a seat |
| POST | `/bookings/{id}/confirm` | Confirm booking |
| POST | `/bookings/{id}/cancel` | Cancel booking |
| GET | `/bookings` | List user bookings |

## 🐳 Docker Deployment

### Local Development
```bash
docker-compose up --build
```

### Production
```bash
docker-compose -f docker-compose.prod.yml up -d
```

Environment variables (see `.env.example`):
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` (64-char hex)

## ☁️ Cloud Deployment

### Render (Free Tier)
1. Create PostgreSQL database on Render
2. Create Web Service, connect GitHub repo
3. Build: `./mvnw clean package -DskipTests`
4. Start: `java -jar target/tech.mahesh.jar`
5. Set environment variables

### AWS EC2 + RDS
Documented in `docs/deployment.md`

## 🧪 Testing

```bash
# Unit & Integration tests
./mvnw test

# Load testing with k6
k6 run k6-stress-test.js
```

### Load Test Scenarios
1. **Contention Race**: 50 VUs same seat → 1 success, 49 409s
2. **Sustained Read**: 20 VUs 15s → all < 300ms
3. **Spike**: 0→30 VUs auth → all < 500ms

## 📁 Project Structure

```
src/main/java/tech/mahesh/slotify/
├── config/          # Security, OpenAPI config
├── controller/      # REST controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA entities
├── enums/           # Enum types
├── exception/       # Global exception handler
├── repository/      # Spring Data repositories
├── security/        # JWT auth components
└── service/         # Business logic
```

## 📚 Documentation

- `docs/developer-journal.md` - 14-day development log
- `docs/adr/` - Architecture Decision Records (5)
- `docs/architecture.md` - System design
- `docs/deployment.md` - Deployment guide
- `docs/testing.md` - Testing strategy
- `docs/DECISIONS.md` - Decision log

## 🔮 Future Improvements

- Redis-based distributed locking for multi-instance consistency
- Queue-based slot reservation (Kafka/RabbitMQ) for extreme traffic
- Idempotency Keys on lock-slot endpoint
- Prometheus + Grafana monitoring dashboards

## License

MIT