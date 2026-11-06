# Slotify API Reference

## Authentication

All booking endpoints require JWT Bearer token.

```
Authorization: Bearer <token>
```

## Endpoints

### Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /auth/register | Register new user |
| POST | /auth/login | Login, returns JWT |

### Events

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /events | List events (paginated) |
| GET | /events/{id} | Get event details |

### Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /bookings/lock-seat | Lock a seat |
| POST | /bookings/{id}/confirm | Confirm booking |
| POST | /bookings/{id}/cancel | Cancel booking |
| GET | /bookings | List user bookings |

## Error Responses

| Code | Status | Description |
|------|--------|-------------|
| CONFLICT | 409 | Seat already booked / optimistic lock conflict |
| SEAT_NOT_AVAILABLE | 409 | Seat not available for booking |
| BOOKING_CONFLICT | 409 | Booking operation conflict |
| VALIDATION_ERROR | 400 | Request validation failed |
| INTERNAL_ERROR | 500 | Unexpected server error |

Example error response:
```json
{
  "code": "CONFLICT",
  "message": "Seat was booked by another user. Please try again.",
  "status": 409
}
```
