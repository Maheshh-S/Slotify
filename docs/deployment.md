# Slotify Deployment Guide

## Local Development

### Using Docker Compose (Recommended)

```bash
docker-compose up --build
```

API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html

### Using Maven Locally

```bash
./mvnw spring-boot:run
```

Uses H2 in-memory database. Swagger: http://localhost:8080/swagger-ui.html

## Production Deployment

### Render (Free Tier)

1. Create PostgreSQL database on Render
2. Create Web Service, connect GitHub repo
3. Build Command: `./mvnw clean package -DskipTests`
4. Start Command: `java -jar target/tech.mahesh.jar`
4. Set environment variables:
   - DB_URL
   - DB_USERNAME
   - DB_PASSWORD
   - JWT_SECRET

### Docker Production

```bash
docker-compose -f docker-compose.prod.yml up -d
```

Required environment variables (see .env.example):
- DB_URL, DB_USERNAME, DB_PASSWORD
- JWT_SECRET (64-char hex)

## AWS EC2 + RDS

1. Launch RDS PostgreSQL instance
2. Launch EC2 instance with Docker
3. Configure security groups
4. Deploy using docker-compose.prod.yml
5. Set up nginx reverse proxy with SSL

## CI/CD Pipeline

GitHub Actions workflow (`.github/workflows/ci.yml`):
1. Build & test with PostgreSQL service
2. Build Docker image
3. Push to GHCR
4. Deploy to Render (on main branch push)

Required GitHub secrets:
- JWT_SECRET
- RENDER_API_KEY
- RENDER_SERVICE_ID
