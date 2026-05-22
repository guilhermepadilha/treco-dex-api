/*
 * Copyright (c) 2026 Guilherme Santos Padilha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    checkstyle
}

group = "com.treco.dex.api"
version = "0.1.0-SNAPSHOT"

checkstyle {
    toolVersion = "10.12.5"
    isIgnoreFailures = true
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Web & Security
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core:9.22.3")
    
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
    
    // Rate Limiting
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")
    
    // AI/ML Support
    implementation("dev.langchain4j:langchain4j-core:0.31.0")
    implementation("dev.langchain4j:langchain4j-open-ai:0.31.0")
    implementation("dev.langchain4j:langchain4j:0.31.0")
    
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

tasks.test {
    // Passa as variáveis para o processo de teste
    environment("DOCKER_HOST", "unix:///var/run/docker.sock")
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
    systemProperty("api.version", "1.40")
    // Se quiser reutilizar contêineres entre execuções:
    // environment("TESTCONTAINERS_REUSE_ENABLE", "true")
    
    // Opcional: executa o teste sem daemon (evita “stale” env)
    // useJUnitPlatform()

    // options.encoding = "UTF-8" // removed: Test task does not have 'options'
}

tasks.register("printDockerInfo") {

    doLast {
        println("DOCKER_HOST = ${System.getenv("DOCKER_HOST")}")
        println("Docker socket exists = ${File("/var/run/docker.sock").exists()}")
    }
}



tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

}

tasks.withType<Test> {
    useJUnitPlatform()
}
