# Stage 1: Build application with Maven
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy maven wrapper & pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Copy source code and package
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/jurisprudence-hub-be-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Production container JVM flags optimized for 512MB RAM environments (Render Free):
# -XX:+UseSerialGC: minimizes native GC thread memory and overhead on single-core / low RAM
# Default: 256MB max heap, 96MB metaspace, 256KB stack, leaving ~150MB buffer for OS and native memory
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:--Xms128m -Xmx256m -XX:MaxMetaspaceSize=96m -Xss256k -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError} -Dserver.port=${PORT:-8080} -jar app.jar"]
