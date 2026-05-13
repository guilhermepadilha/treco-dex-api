plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.trecodex"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Web & Security
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("org.flywaydb:flyway-database-postgresql:9.22.3")
    
    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
    
    // Logging & Observability
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.opentelemetry:opentelemetry-api:1.34.1")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.0.0")
    
    // JWT & Auth
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // AI/ML Support
    implementation("dev.langchain4j:langchain4j-core:0.31.0")
    
    // Utilities
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:testcontainers:1.19.7")
    testImplementation("org.testcontainers:postgresql:1.19.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
