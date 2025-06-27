# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spare Change/Round-Up Investment Application that automatically rounds up transactions to the nearest dollar and invests the spare change in cryptocurrency (Bitcoin or Ethereum) via Coinbase when a threshold is reached.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.1.5, H2 Database, Flyway
- **Frontend**: React 18, Vite, Mantine UI, React Query
- **External API**: Coinbase Exchange API (sandbox)
- **Deployment**: Docker Compose, Render

## Essential Commands

### Frontend Development
```bash
cd frontend
npm install          # Install dependencies
npm run dev          # Start dev server (port 5173)
npm run build        # Build for production
npm run lint         # Run ESLint
```

### Backend Development
```bash
cd spare-change/spare-change-api
mvn clean install    # Build and run tests
mvn spring-boot:run  # Start application (port 8080)
mvn test             # Run all tests
mvn test -Dtest=TestClassName  # Run specific test
mvn package          # Create JAR file
```

### Docker
```bash
docker-compose up    # Start both services
docker-compose build # Build containers
```

## Architecture Overview

### Backend Architecture
The backend follows a layered architecture pattern:

1. **Controller Layer** (`controller/`): REST endpoints handling HTTP requests
2. **Service Layer** (`service/`): Business logic implementation
3. **Repository Layer** (`repository/`): Data access using Spring Data JPA
4. **Client Layer** (`client/`): External API integration (Coinbase)

Key services:
- `TxService`: Manages transaction creation and spare change calculation
- `ThresholdService`: Monitors accumulated change and triggers cryptocurrency purchases
- `CoinbaseClient`: Handles cryptocurrency order execution (supports BTC-USD and ETH-USD)

### Frontend Architecture
React application with:
- Component-based architecture in `frontend/src/components/`
- Custom hooks in `frontend/src/hooks/` for real-time SSE updates
- API utilities in `frontend/src/utils/` for backend communication

### Data Flow
1. User adds transaction via UI
2. Backend calculates spare change (rounds up to next dollar)
3. When accumulated change reaches threshold, system automatically creates cryptocurrency buy order
4. Real-time updates sent to frontend via Server-Sent Events (SSE)
5. Users can switch between BTC-USD and ETH-USD products via configuration endpoint

### Database Schema
Managed by Flyway migrations. Main entities:
- `Tx`: Transaction records with amount, description, status, spare change amount
- `RoundupSummary`: Cryptocurrency purchase orders with crypto amounts and product type
- `MockOrder`: Simulated orders when Coinbase API is unavailable

Transaction states: NEW → ROUNDUP_APPLIED

### Key Features
- **Multi-cryptocurrency support**: Switch between BTC-USD and ETH-USD
- **Automatic fallback**: Uses simulated orders when Coinbase API is unavailable
- **Dynamic configuration**: Update threshold and product at runtime
- **Transaction ordering**: NEW transactions appear first, then by date (latest first)
- **User-friendly responses**: Human-readable messages for threshold checks

### Configuration
- Runtime configuration updates via `/api/configuration` endpoint
- Environment variables for Coinbase API credentials (see COINBASE_SETUP.md)
- Application properties in `application.yaml`

## Testing Strategy

Backend tests located in `spare-change/spare-change-api/src/test/`:
- Unit tests for individual components
- Integration tests for full flow validation (e.g., `SpareChangeSuccessfulFlowTest`)
- Coinbase API integration tests

Run specific test: `mvn test -Dtest=SpareChangeSuccessfulFlowTest`

## Important Files

- `/spare-change/spare-change-api/API_DOCUMENTATION.md` - Complete API endpoint reference
- `/spare-change/spare-change-api/COINBASE_SETUP.md` - Coinbase integration setup guide
- `/spare-change/spare-change-api/src/main/resources/db/migration/` - Database schema
- `/frontend/src/App.jsx` - Main React application entry point
- `/docker-compose.yml` - Container orchestration configuration