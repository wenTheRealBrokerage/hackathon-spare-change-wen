# Spare Change Application - Development TODO

## Project Overview
A financial application that rounds up transactions to the nearest dollar and invests the spare change into cryptocurrency via Coinbase.

## Current State Analysis
- **Backend**: Spring Boot application with JPA/Hibernate
- **Frontend**: React with Vite, Mantine UI, and WebSocket support
- **Database**: SQL database with Flyway migrations
- **Architecture**: Microservices with real-time updates via WebSockets

## Core Development Tasks

### 1. Backend API Development

#### Transaction Management
- [ ] Implement REST controllers for transaction endpoints
  - [ ] POST `/api/transactions` - Create new transaction
  - [ ] GET `/api/transactions` - List transactions with pagination
  - [ ] GET `/api/transactions/{id}` - Get transaction details
  - [ ] PUT `/api/transactions/{id}/status` - Update transaction status
- [ ] Add validation for transaction amounts and merchant data
- [ ] Implement transaction filtering and search capabilities
- [ ] Add bulk transaction import functionality

#### Coinbase Integration
- [ ] Create Coinbase service integration
  - [ ] Implement OAuth2 authentication flow
  - [ ] Add Coinbase Commerce API client
  - [ ] Create order placement functionality
  - [ ] Implement webhook handlers for order status updates
- [ ] Add configuration for API keys and endpoints
- [ ] Implement retry logic and error handling
- [ ] Create order status synchronization job

#### Round-Up Logic
- [ ] Enhance spare change calculation logic
  - [ ] Support different rounding strategies (up, nearest 5, etc.)
  - [ ] Add minimum/maximum spare change limits
  - [ ] Implement daily/weekly aggregation options
- [ ] Create RoundUpSummary service implementation
- [ ] Add scheduled job for automatic round-up processing

#### Security & Authentication
- [ ] Implement Spring Security configuration
  - [ ] Add JWT authentication
  - [ ] Create user registration/login endpoints
  - [ ] Implement role-based access control
- [ ] Add API rate limiting
- [ ] Implement request validation and sanitization

### 2. Frontend Development

#### Component Implementation
- [ ] Create TransactionTab component
  - [ ] Transaction list with real-time updates
  - [ ] Transaction form for manual entry
  - [ ] Spare change visualization
- [ ] Create CryptoOrdersTab component
  - [ ] Display Coinbase order history
  - [ ] Show order status and details
  - [ ] Add order cancellation functionality
- [ ] Implement dashboard with statistics
  - [ ] Total spare change accumulated
  - [ ] Investment performance metrics
  - [ ] Transaction trends charts

#### WebSocket Integration
- [ ] Set up STOMP client configuration
- [ ] Implement real-time transaction updates
- [ ] Add connection status indicator
- [ ] Handle reconnection logic

#### State Management
- [ ] Configure React Query for API calls
- [ ] Implement optimistic updates
- [ ] Add proper error handling
- [ ] Create loading states

### 3. Database & Infrastructure

#### Database Schema
- [ ] Add user table and relationships
- [ ] Create indexes for performance optimization
- [ ] Add audit columns (created_by, updated_at)
- [ ] Implement soft deletes

#### Docker Configuration
- [ ] Create docker-compose.yml for local development
  - [ ] Backend service
  - [ ] Frontend service
  - [ ] Database service
  - [ ] Redis for caching (if needed)
- [ ] Add environment-specific configurations
- [ ] Create health check endpoints

#### Monitoring & Logging
- [ ] Add structured logging (Logback/SLF4J)
- [ ] Implement application metrics (Micrometer)
- [ ] Add error tracking (Sentry integration)
- [ ] Create monitoring dashboards

### 4. Testing

#### Backend Testing
- [ ] Unit tests for services
- [ ] Integration tests for controllers
- [ ] Repository layer tests
- [ ] Coinbase API mock tests

#### Frontend Testing
- [ ] Component unit tests
- [ ] Integration tests for API calls
- [ ] E2E tests with Cypress/Playwright

### 5. Configuration & Environment

#### Application Properties
- [ ] Create application.yml for different profiles
- [ ] Add Coinbase API configuration
- [ ] Configure database connection pooling
- [ ] Set up CORS settings

#### Environment Variables
- [ ] Document all required environment variables
- [ ] Create .env.example file
- [ ] Add secrets management

### 6. Documentation

- [ ] API documentation with OpenAPI/Swagger
- [ ] Frontend component documentation
- [ ] Deployment guide
- [ ] User manual
- [ ] Contributing guidelines

### 7. CI/CD Pipeline

- [ ] GitHub Actions workflow
  - [ ] Build and test on PR
  - [ ] Security scanning
  - [ ] Docker image building
  - [ ] Automated deployment

### 8. Additional Features (Phase 2)

- [ ] Multi-currency support
- [ ] Recurring investment schedules
- [ ] Investment portfolio tracking
- [ ] Tax reporting functionality
- [ ] Mobile app development
- [ ] Bank account integration via Plaid

## Getting Started for Development

1. **Prerequisites**
   - Java 17+
   - Node.js 18+
   - Maven 3.8+
   - Docker & Docker Compose
   - Coinbase Commerce account

2. **Environment Setup**
   ```bash
   # Backend
   cd spare-change/spare-change-api
   cp .env.example .env
   # Add Coinbase API credentials
   
   # Frontend
   cd frontend
   npm install
   ```

3. **Database Setup**
   ```bash
   # Run migrations
   mvn flyway:migrate
   ```

4. **Running the Application**
   ```bash
   # Start all services
   docker-compose up
   
   # Or run separately
   # Backend
   mvn spring-boot:run
   
   # Frontend
   npm run dev
   ```

## Priority Order

1. **High Priority**
   - Basic CRUD operations for transactions
   - Coinbase API integration
   - User authentication
   - Core UI components

2. **Medium Priority**
   - Real-time updates
   - Advanced filtering/search
   - Performance optimization
   - Comprehensive testing

3. **Low Priority**
   - Additional features
   - Mobile app
   - Advanced analytics

## Notes

- Ensure PCI compliance for handling financial data
- Follow security best practices for API keys
- Implement proper error handling throughout
- Consider regulatory requirements for financial applications