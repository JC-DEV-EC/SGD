# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy Maven descriptor and resolve dependencies first (better layer caching)
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -q -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre

# Create non-root user and group
RUN groupadd --system appgroup \
    && useradd --system --create-home --gid appgroup appuser

WORKDIR /app

# Copy the built jar
COPY --from=builder /app/target/sgd-backend-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose HTTP port
EXPOSE 8080

# Drop privileges
USER appuser

# Use a read-only filesystem at runtime if the platform supports it (Render sí lo soporta)
# You can enable it in Render settings with "Read Only Root Filesystem".

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
