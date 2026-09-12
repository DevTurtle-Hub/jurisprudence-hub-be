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

ENV PORT=10000
EXPOSE 10000

# Production container JVM flags optimized for 512MB RAM environments (Render Free):
# -XX:+UseSerialGC: minimizes native GC thread memory and overhead on single-core / low RAM
# Default: 224MB max heap, 160MB metaspace, 256KB stack, leaving ~100MB buffer for OS and native memory
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:--Xms96m -Xmx224m -XX:MaxMetaspaceSize=160m -Xss256k -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Djava.net.preferIPv4Stack=true} -Dserver.port=${PORT:-10000} -Dserver.address=0.0.0.0 -jar app.jar"]
