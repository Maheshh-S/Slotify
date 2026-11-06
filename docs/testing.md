# Slotify Testing Strategy

## Test Types

### Unit Tests
- Location: `src/test/java/tech/mahesh/slotify/service/`
- BookingServiceTest: Optimistic locking, concurrency, idempotency
- Uses H2 in-memory with `create-drop` DDL

### Integration Tests
- Location: `src/test/java/tech/mahesh/slotify/controller/`
- AuthControllerTest: Register, login flows
- BookingControllerTest: Lock, confirm, cancel, pagination
- Uses TestRestTemplate with random port

### Load Testing
- Tool: k6
- Script: `k6-stress-test.js`
- Scenarios:
  1. Contention race: 50 VUs same seat → 1 success, 49 409s
  2. Sustained read: 20 VUs 15s → all < 300ms
  3. Spike: 0→30 VUs auth → all < 500ms

Run load tests:
```bash
k6 run k6-stress-test.js
```

## Running Tests

```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=BookingServiceTest

# Specific test method
./mvnw test -Dtest=BookingServiceTest#concurrentLockSeat_onlyOneSucceeds
```

## Test Configuration

Tests use `application-test.yml` with:
- H2 in-memory database
- Flyway disabled
- Hibernate `create-drop` DDL
- JWT secret for testing
