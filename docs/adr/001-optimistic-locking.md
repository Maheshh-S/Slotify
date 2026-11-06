# ADR 001: Optimistic Locking for Seat Booking

## Status
Accepted

## Context
Need to prevent double-booking when multiple users try to book the same seat simultaneously. Two main approaches:
- Pessimistic locking (SELECT FOR UPDATE) - blocks other transactions
- Optimistic locking (@Version) - allows concurrent reads, fails on conflict

## Decision
Use optimistic locking with @Version on Seat entity. Booking attempts use version check in UPDATE WHERE clause. Conflict returns HTTP 409.

## Consequences
+ Better throughput under high read load
+ No thread blocking, no deadlocks
+ Horizontal scaling friendly
- Higher retry rate under contention
- Clients must handle 409 with exponential backoff

## Alternatives Considered
- Pessimistic locking: rejected due to scaling concerns
- Redis distributed lock: overkill for MVP, adds infrastructure
