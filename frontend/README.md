# Transaction Monitor Frontend

A React 18 application built with Vite, Mantine UI, React Query, and real-time streaming capabilities.

## Features

- **Transaction Management**
  - Add random transactions with merchant and amount
  - Manual transaction entry with custom merchant and amount
  - Live transaction list with Server-Sent Events (SSE)
  - Displays transaction amount, spare change, and status
  - Shows total accumulated spare change for NEW transactions
  - Adjustable page size (5 rows default, +/- buttons)
  - Auto-refreshes every 2 seconds to show status updates
  
- **Threshold Controls**
  - "Check spare change" button - Triggers manual threshold check
  - Editable buy threshold (click edit icon to change)
  - Valid threshold range: $0.01 to $1000.00
  - If spare change total >= threshold, initiates Coinbase BTC purchase
  - Toast notifications for all actions
  - Note: Threshold changes are temporary and reset on backend restart

- **Crypto Orders**
  - Displays round-up orders from Coinbase
  - Shows total orders count and total USD converted
  - Polls `/roundup/orders/all` and `/roundup/summary` every 3 seconds
  - Displays order details including Coinbase order ID

## Tech Stack

- Vite + React 18
- Mantine UI for components
- React Query for data fetching
- Server-Sent Events for live streaming

## Backend Integration

This frontend connects to the Spring Boot backend running on `http://localhost:8080`.

### API Endpoints Used:
- `POST /tx` - Create new transaction
- `GET /tx` - Get paginated transactions (polled for status updates)
- `GET /tx/stream` - SSE endpoint for live transactions
- `POST /cron/threshold` - Trigger manual threshold check
- `GET /config/threshold` - Get current threshold configuration
- `PUT /config/threshold` - Update threshold value
- `GET /roundup/orders/all` - Get all round-up orders
- `GET /roundup/summary` - Get round-up summary statistics
- `GET /roundup/coinbase/orders/btc` - Get Coinbase BTC orders

## Getting Started

1. Make sure the Spring Boot backend is running on port 8080
2. Install dependencies and start the frontend:

```bash
cd frontend
npm install
npm run dev
```

The app runs on http://localhost:5173 with proxy configured for backend API calls.

## Notes

- The backend automatically calculates spare change (round-up amount) for each transaction
- When spare change totals reach $5, the backend triggers a Coinbase buy order
- Transaction status changes from "NEW" to "ROUNDUP_APPLIED" after processing