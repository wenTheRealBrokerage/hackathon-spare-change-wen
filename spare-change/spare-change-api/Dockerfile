# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom.xml if exists
COPY ../pom.xml ../pom.xml 2>/dev/null || true

# Copy module pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/spare-change-api-*.jar app.jar

# Create a non-root user
RUN groupadd -r spring && \
    useradd -r -g spring spring

# Set Java options for better container performance and to handle CORS/HTTP methods properly
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

USER spring:spring

# Render uses dynamic ports
EXPOSE ${PORT:-8080}

# Use shell form to expand environment variables
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]