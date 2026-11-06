# ADR 002: JWT Stateless Authentication

## Status
Accepted

## Context
Need authentication for booking endpoints. Options:
- Session-based (server-side state)
- JWT (stateless, client-side state)

## Decision
Use JWT with HS256, 1-hour expiry. Stateless - no server-side session store. Spring Security filter chain with OncePerRequestFilter.

## Consequences
+ Horizontally scalable - no sticky sessions
+ Simple deployment - no Redis/session store
- Token revocation requires blocklist or short expiry
- Payload size in every request
