# Decision Log

| Date | Decision | Rationale |
|------|----------|-----------|
| Oct 24 | Java 21 + Spring Boot 3.2.4 | LTS versions, modern features |
| Oct 24 | Optimistic locking (@Version) | Horizontal scaling, no deadlocks |
| Oct 24 | JWT stateless auth | Horizontal scaling, no session store |
| Oct 24 | Flyway migrations | Explicit schema versioning |
| Oct 24 | Multi-stage Docker | Small production image |
| Oct 25 | UTC timezone enforced | Distributed system consistency |
| Oct 26 | @Version on Seat entity | Optimistic locking implementation |
| Oct 26 | Composite index (status, locked_at) | Cleanup scheduler performance |
| Oct 27 | JWT with HS256, 1hr expiry | Balance security/usability |
| Oct 27 | Stateless sessions, CSRF disabled | REST API best practice |
| Oct 28 | Idempotent confirm/cancel | Retry safety |
| Oct 28 | SeatLockCleanupService @Scheduled | Auto-cleanup stale locks |
| Oct 29 | EntityGraph for N+1 prevention | Query optimization |
| Oct 30 | Multi-stage Docker (JDK→JRE) | Small prod image |
| Oct 30 | GitHub Actions CI/CD | Automated testing/deployment |
| Oct 30 | Render deployment target | Free tier, easy setup |
| Oct 31 | Flyway baseline-on-migrate | Works with existing DB |
| Oct 31 | Seed data for testing | Realistic load test data |
| Nov 1 | H2 for tests, create-drop | Fast, isolated tests |
| Nov 1 | @DirtiesContext for isolation | Test independence |
| Nov 2 | k6 for load testing | Scriptable, CI-friendly |
| Nov 3 | GlobalExceptionHandler for 409 | Consistent error responses |
| Nov 4 | OpenAPI with JWT security | Interactive API docs |
| Nov 4 | Postman collection | Easy API testing |
| Nov 5 | DTO separation from entities | Clean architecture |
| Nov 6 | Comprehensive README | Onboarding ease |
