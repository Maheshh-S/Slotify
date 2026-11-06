# ADR 005: k6 for Load Testing

## Status
Accepted

## Context
Need to validate optimistic locking under contention and measure performance.

## Decision
k6 script with 3 scenarios: contention race (50 VUs same seat), sustained read (20 VUs 15s), spike (0->30 VUs auth). Thresholds: p90 < 300ms, p95 < 500ms.

## Consequences
+ Validates correctness (409 rate) and performance
+ CI-friendly, scriptable
- External tool dependency
- Need running service to test
