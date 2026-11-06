# ADR 004: Multi-stage Docker Build

## Status
Accepted

## Context
Need small production image. Maven build needs JDK, runtime only needs JRE.

## Decision
Stage 1: eclipse-temurin:21-jdk-alpine (build with Maven, cache deps). Stage 2: eclipse-temurin:21-jre-alpine (copy JAR only). Final image ~150MB.

## Consequences
+ Small production image
+ Build cache via dependency:go-offline
+ Build and runtime separation
- Slightly more complex Dockerfile
