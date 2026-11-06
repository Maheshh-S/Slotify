# Slotify - Architecture Overview

## System Design

Slotify is a concurrency-safe slot booking system built with Spring Boot 3.2.4, Java 21, PostgreSQL 15.

### Core Principle: Optimistic Locking

The system prevents double-booking without database locks. Each Seat entity carries a @Version field. Booking attempts use a version check in the UPDATE WHERE clause:

```sql
UPDATE seat SET status='LOCKED', locked_at=:now 
WHERE id=:id AND version=:version AND status='AVAILABLE'
```

If rows affected = 0, another transaction won → return HTTP 409 Conflict.

### Components

- **AuthController**: Register/login → JWT
- **EventController**: List/get events (cached, EntityGraph)
- **BookingController**: Lock/confirm/cancel bookings, user bookings
- **BookingService**: Core logic, @Transactional, idempotent operations
- **SeatLockCleanupService**: @Scheduled every 5min, releases stale LOCKED seats
- **GlobalExceptionHandler**: Maps exceptions to HTTP responses

### Data Flow

```
Client → JWT Filter → Controller → Service → Repository → PostgreSQL
                    ↓
              409 Conflict if version mismatch
```

### Deployment

- Local: docker-compose.yml (PostgreSQL + App)
- Production: docker-compose.prod.yml + Render (auto-deploy via GitHub Actions)
- CI/CD: GitHub Actions → GHCR → Render

### Scaling

Stateless app + PostgreSQL advisory locks → horizontal scaling ready.
