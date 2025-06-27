# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first to leverage Docker cache
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

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]