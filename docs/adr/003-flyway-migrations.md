# ADR 003: Flyway for Database Migrations

## Status
Accepted

## Context
Schema evolution needs version control. Hibernate ddl-auto=update is risky in production.

## Decision
Flyway with versioned SQL migrations. V1__init_schema.sql, V2__seed_data.sql. baseline-on-migrate=true. ddl-auto=validate.

## Consequences
+ Explicit, reviewable schema changes
+ Repeatable across environments
+ Rollback via manual SQL if needed
- Manual SQL writing required
