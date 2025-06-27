# 🪙 Spare Change - Micro-Investment Platform

> Automatically round up your transactions and invest the spare change in cryptocurrency

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)
![React](https://img.shields.io/badge/React-18-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## 🚀 Overview

Spare Change is a modern micro-investment application that automatically rounds up your transactions to the nearest dollar and invests the accumulated spare change in cryptocurrency (Bitcoin or Ethereum) through Coinbase. Perfect for building wealth through small, automatic investments.

### ✨ Key Features

- **Automatic Round-Ups**: Every transaction is rounded up to the nearest dollar
- **Multi-Cryptocurrency Support**: Choose between Bitcoin (BTC) or Ethereum (ETH)
- **Smart Threshold Investing**: Automatically invests when spare change reaches your set threshold
- **Real-Time Updates**: Live price ticker and transaction status updates via Server-Sent Events
- **Coinbase Integration**: Secure integration with Coinbase Exchange API
- **Fallback Mode**: Simulated orders when Coinbase API is unavailable
- **Modern UI**: Futuristic dark theme with neon accents and smooth animations

## 🛠️ Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.1.5**
- **H2 Database** (in-memory)
- **Flyway** for database migrations
- **Spring Data JPA** for data persistence
- **Coinbase Exchange API** integration

### Frontend
- **React 18** with **Vite**
- **Mantine UI** component library
- **React Query** for data fetching
- **Server-Sent Events** for real-time updates

### DevOps
- **Docker Compose** for containerization
- **GitHub Actions** for CI/CD
- **Render** for deployment

## 🏃‍♂️ Quick Start

### Prerequisites
- Java 17+
- Node.js 16+
- Maven 3.6+
- Docker & Docker Compose (optional)

### Clone the Repository
```bash
git clone https://github.com/yourusername/spare-change.git
cd spare-change
```

### Option 1: Docker Compose (Recommended)
```bash
docker-compose up
```
- Frontend: http://localhost:5173
- Backend: http://localhost:8080

### Option 2: Manual Setup

#### Backend
```bash
cd spare-change/spare-change-api
mvn clean install
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 📋 Configuration

### Coinbase API Setup
1. Create a [Coinbase Sandbox Account](https://public.sandbox.exchange.coinbase.com)
2. Generate API credentials
3. Set environment variables:
```bash
export COINBASE_API_KEY=your_api_key
export COINBASE_API_SECRET=your_api_secret
export COINBASE_API_PASSPHRASE=your_passphrase
```

See [COINBASE_SETUP.md](spare-change/spare-change-api/COINBASE_SETUP.md) for detailed instructions.

### Application Settings
- **Threshold Amount**: Configure via API or UI (default: $5.00)
- **Cryptocurrency**: Switch between BTC-USD and ETH-USD
- **Database**: H2 in-memory (data resets on restart)

## 🏗️ Architecture

### System Flow
```
User Transaction → Round-Up Calculation → Accumulation → 
Threshold Check → Cryptocurrency Purchase → Portfolio Update
```

### API Endpoints
- `POST /transactions` - Add new transaction
- `GET /transactions` - List all transactions
- `GET /roundup-summary` - View investment orders
- `PUT /config/threshold` - Update investment threshold
- `PUT /config/product` - Switch cryptocurrency (BTC/ETH)
- `GET /sse/transactions` - Real-time transaction updates

Full API documentation: [API_DOCUMENTATION.md](spare-change/spare-change-api/API_DOCUMENTATION.md)

## 🧪 Testing

### Backend Tests
```bash
cd spare-change/spare-change-api
mvn test                              # Run all tests
mvn test -Dtest=TestClassName         # Run specific test
```

### Frontend Tests
```bash
cd frontend
npm run test
```

## 📸 Screenshots

### Dashboard
- Real-time Bitcoin price ticker
- Transaction list with spare change tracking
- Progress bar showing accumulation toward threshold
- Round-up order history

### Features in Action
- Add random or manual transactions
- Watch spare change accumulate in real-time
- Automatic cryptocurrency purchases at threshold
- Switch between Bitcoin and Ethereum investments

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- UI powered by [Mantine](https://mantine.dev/)
- Cryptocurrency trading via [Coinbase](https://www.coinbase.com/)

## 📧 Contact

Questions? Issues? Feel free to [open an issue](https://github.com/yourusername/spare-change/issues) or reach out!

---

**Note**: This is a demonstration/educational project. Please ensure compliance with financial regulations in your jurisdiction before deploying to production.