# Multi-stage build for both frontend and backend

# Stage 1: Build backend
FROM maven:3.8.5-openjdk-17 AS backend-build
WORKDIR /app/backend
COPY spare-change/spare-change-api/pom.xml .
RUN mvn dependency:go-offline
COPY spare-change/spare-change-api/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Build frontend
FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# Stage 3: Final image with both services
FROM openjdk:17-jdk-slim

# Install nginx and supervisor
RUN apt-get update && apt-get install -y \
    nginx \
    supervisor \
    && rm -rf /var/lib/apt/lists/*

# Copy backend jar
COPY --from=backend-build /app/backend/target/*.jar /app/backend/app.jar

# Copy frontend build to nginx
COPY --from=frontend-build /app/frontend/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy supervisor configuration
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Expose port 80 (nginx will proxy to backend)
EXPOSE 80

# Start supervisor
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]