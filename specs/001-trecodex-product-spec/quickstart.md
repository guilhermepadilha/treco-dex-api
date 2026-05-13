# Quickstart: TrecoDex Backend Platform

## Prerequisites

- Java 21 LTS
- Gradle 8.x
- Docker
- PostgreSQL 15+ with `pgvector` extension
- Redis
- Kafka

## Local Development

1. Clone the repository.
2. Create the required services:
   - PostgreSQL with `pgvector`
   - Redis
   - Kafka
3. Configure environment variables:
   - `DATABASE_URL=jdbc:postgresql://localhost:5432/trecodex`
   - `DATABASE_USERNAME=trecodex`
   - `DATABASE_PASSWORD=trecodex`
   - `REDIS_URL=redis://localhost:6379`
   - `KAFKA_BOOTSTRAP_SERVERS=localhost:9092`
   - `JWT_SECRET=<secure-secret>`
   - `SPRING_PROFILES_ACTIVE=local`
4. Run database migrations and seed initial data.
5. Start the application:
   ```bash
   ./gradlew bootRun
   ```

## Testing

- Run unit and integration tests:
  ```bash
  ./gradlew test
  ```

## Notes

- The initial platform is designed as a modular monolith, so local execution is supported without an orchestrated distributed deployment.
- Use Docker Compose or Kubernetes later for full stack development when Kafka and Redis are required.
- The backend exposes versioned REST APIs and should be consumed by mobile or web clients through stable contract definitions.
