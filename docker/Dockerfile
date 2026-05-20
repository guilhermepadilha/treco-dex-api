FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy built JAR
COPY build/libs/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose port
EXPOSE 8080
