# Multi-stage build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/tech.mahesh.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

LABEL org.opencontainers.image.title="Slotify"
LABEL org.opencontainers.image.description="Concurrency-Safe Slot Booking System"
LABEL org.opencontainers.image.vendor="tech.mahesh"

ENTRYPOINT ["java", "-jar", "app.jar"]