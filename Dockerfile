# Multi-stage build for smaller image size
FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src src
RUN mvn clean package -DskipTests -q

# Production stage - use Alpine for smaller size
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install only essential tools (removed procps and netcat-openbsd as they're rarely needed)
RUN apk add --no-cache curl

RUN apk add --no-cache bash

# Create appuser
RUN addgroup -S appuser && adduser -S appuser -G appuser

# Copy jar from builder stage
COPY --from=builder /app/target/public-transport-management-system-*.jar app.jar

# Create logs directory and set permissions
RUN mkdir -p logs && chown -R appuser:appuser logs app.jar

# Copy and set up entrypoint script
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh && chown appuser:appuser /app/docker-entrypoint.sh

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Optimized JVM settings for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

# Health check (optional but recommended)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/app/docker-entrypoint.sh"]