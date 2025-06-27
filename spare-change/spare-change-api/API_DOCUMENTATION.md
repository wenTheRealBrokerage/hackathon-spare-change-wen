# Spare Change API Documentation

## Base URL
```
http://localhost:8080
```

## Endpoints

### Transaction Management

#### Create Transaction
```
POST /tx
Content-Type: application/json

{
  "merchant": "Coffee Shop",
  "amountUsd": 12.75
}
```
Response:
```json
{
  "id": 1,
  "merchant": "Coffee Shop",
  "amountUsd": 12.75,
  "ts": "2025-06-27T14:30:00",
  "spareUsd": 0.25,
  "status": "NEW",
  "coinbaseOrderId": null
}
```

#### List Transactions
```
GET /tx?page=0&size=20&sort=ts,desc
```
Response:
```json
{
  "content": [
    {
      "id": 1,
      "merchant": "Coffee Shop",
      "amountUsd": 12.75,
      "ts": "2025-06-27T14:30:00",
      "spareUsd": 0.25,
      "status": "NEW",
      "coinbaseOrderId": null
    }
  ],
  "pageable": {...},
  "totalElements": 1,
  "totalPages": 1
}
```

#### Stream Transactions (Server-Sent Events)
```
GET /tx/stream
Accept: text/event-stream
```
Real-time stream of new transactions as they are created.

### Round-Up Management

#### Trigger Manual Threshold Check
```
POST /cron/threshold
```
Response:
```json
{
  "executed": true,
  "message": "Threshold check executed successfully"
}
```

#### List Round-Up Orders (Paginated)
```
GET /roundup/orders?page=0&size=20
```
Response:
```json
{
  "content": [
    {
      "id": 1,
      "totalUsd": 5.75,
      "createdAt": "2025-06-27T14:35:00",
      "coinbaseOrderId": "abc123-def456"
    }
  ],
  "pageable": {...},
  "totalElements": 1,
  "totalPages": 1
}
```

#### List All Round-Up Orders
```
GET /roundup/orders/all
```
Response:
```json
[
  {
    "id": 1,
    "totalUsd": 5.75,
    "createdAt": "2025-06-27T14:35:00",
    "coinbaseOrderId": "abc123-def456"
  },
  {
    "id": 2,
    "totalUsd": 8.25,
    "createdAt": "2025-06-27T15:00:00",
    "coinbaseOrderId": "xyz789-uvw012"
  }
]
```

#### Get Round-Up Summary
```
GET /roundup/summary
```
Response:
```json
{
  "totalOrders": 2,
  "totalUsdConverted": 14.00,
  "orders": [
    {
      "id": 1,
      "totalUsd": 5.75,
      "createdAt": "2025-06-27T14:35:00",
      "coinbaseOrderId": "abc123-def456"
    },
    {
      "id": 2,
      "totalUsd": 8.25,
      "createdAt": "2025-06-27T15:00:00",
      "coinbaseOrderId": "xyz789-uvw012"
    }
  ]
}
```

### Coinbase Order Management (Direct from Exchange)

#### Get All Coinbase Orders
```
GET /roundup/coinbase/orders
```
Response:
```json
[
  {
    "id": "abc123-def456",
    "productId": "BTC-USD",
    "side": "buy",
    "type": "market",
    "status": "done",
    "settled": true,
    "filledSize": 0.0001,
    "executedValue": 5.75,
    "fillFees": 0.05,
    "createdAt": "2025-06-27T14:35:00"
  }
]
```

#### Get BTC-USD Orders Only
```
GET /roundup/coinbase/orders/btc
```
Filters Coinbase orders to show only BTC-USD trading pairs.

#### Get Coinbase Orders Summary
```
GET /roundup/coinbase/orders/summary
```
Response:
```json
{
  "totalCoinbaseOrders": 10,
  "ourRoundUpOrders": 2,
  "allOrders": [...],
  "ourOrders": [
    {
      "id": "abc123-def456",
      "productId": "BTC-USD",
      "status": "done",
      "executedValue": 5.75
    }
  ],
  "ordersByStatus": {
    "done": [...],
    "pending": [...],
    "cancelled": [...]
  }
}
```

#### Get Specific Order Details
```
GET /roundup/coinbase/orders/{orderId}
```
Response:
```json
{
  "coinbaseOrder": {
    "id": "abc123-def456",
    "productId": "BTC-USD",
    "side": "buy",
    "type": "market",
    "status": "done",
    "settled": true,
    "filledSize": 0.0001,
    "executedValue": 5.75,
    "fillFees": 0.05,
    "createdAt": "2025-06-27T14:35:00"
  },
  "localSummary": {
    "id": 1,
    "totalUsd": 5.75,
    "createdAt": "2025-06-27T14:35:00",
    "coinbaseOrderId": "abc123-def456"
  }
}
```

## How It Works

1. **Transaction Creation**: When you create a transaction with `POST /tx`, the system automatically calculates the spare change (round up to next dollar).

2. **Spare Change Accumulation**: All transactions with status `NEW` have their spare change amounts accumulated.

3. **Threshold Monitoring**: 
   - A scheduled task runs every 5 minutes checking if total spare change >= $5.00
   - You can also trigger this manually with `POST /cron/threshold`

4. **Bitcoin Purchase**: When threshold is met:
   - System calls Coinbase API to buy BTC with the accumulated USD
   - Creates a `RoundUpSummary` record with the Coinbase order ID
   - Updates all `NEW` transactions to `ROUNDUP_APPLIED` status

5. **Order Tracking**: Use the `/roundup/*` endpoints to view all BTC purchases made by the system.

## Status Values

- `NEW`: Transaction has spare change that hasn't been used yet
- `ROUNDUP_APPLIED`: Transaction's spare change was included in a BTC purchase

## Configuration Management

### View Current Threshold
```
GET /config/threshold
```
Response:
```json
{
  "currentThreshold": 5.0,
  "defaultThreshold": 5.0,
  "currency": "USD",
  "description": "Minimum spare change amount required to trigger Bitcoin purchase"
}
```

### Update Threshold (Runtime)
```
PUT /config/threshold
Content-Type: application/json

{
  "threshold": "10.00"
}
```
Response:
```json
{
  "previousThreshold": 5.0,
  "newThreshold": 10.0,
  "status": "updated",
  "note": "This change is temporary and will reset on application restart. Update application.yaml for permanent change."
}
```

### View All Configuration
```
GET /config/all
```
Response:
```json
{
  "threshold": {
    "current": 5.0,
    "default": 5.0,
    "min": 0.01,
    "max": 1000.0,
    "currency": "USD"
  },
  "scheduler": {
    "delayMs": 300000,
    "delayMinutes": 5,
    "description": "Frequency of automatic threshold checks"
  },
  "coinbase": {
    "environment": "sandbox",
    "baseUrl": "https://api-public.sandbox.exchange.coinbase.com"
  }
}
```

## Configuration

### Permanent Configuration
Edit `application.yaml`:
```yaml
coinbase:
  api:
    buy-threshold: 5.00  # Minimum USD to trigger BTC purchase
    
stacker:
  delay:
    mas: 300000  # 5 minutes in milliseconds
```

### Runtime Configuration
- Use the `/config/threshold` endpoint to temporarily change the threshold
- Changes are applied immediately to the ThresholdService
- Changes will be lost on application restart