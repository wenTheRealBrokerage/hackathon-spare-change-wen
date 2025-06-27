# TODO.md - Spare Change Application

## Completed Features ✅

### Core Functionality
- [x] Basic spare change calculation and transaction management
- [x] Threshold-based automatic cryptocurrency purchases
- [x] Coinbase Exchange API integration
- [x] Server-Sent Events (SSE) for real-time updates
- [x] Frontend UI with React and Mantine
- [x] Docker containerization
- [x] H2 in-memory database with Flyway migrations

### Recent Enhancements
- [x] Multiple cryptocurrency support (BTC-USD and ETH-USD)
- [x] Dynamic configuration API for switching products
- [x] Automatic fallback to simulated orders when Coinbase unavailable
- [x] User-friendly threshold check responses with emojis
- [x] Transaction ordering (NEW first, then by date)
- [x] Remove DEMO prefix from mock order IDs
- [x] Improved error logging for Coinbase API failures

### Frontend Improvements (Latest)
- [x] Futuristic UI with dark theme and neon accents
- [x] Bitcoin price ticker with 30-second refresh
- [x] Cryptocurrency selector card (BTC/ETH toggle)
- [x] Context-sensitive tooltips throughout UI
- [x] Manual transaction entry modal
- [x] Diagnostic IP display button
- [x] Responsive transaction table with auto-sizing
- [x] Sticky table headers for better UX
- [x] Progress bar showing threshold progress
- [x] Enhanced notifications with remaining amount
- [x] Custom Bitcoin-themed favicon
- [x] Improved error handling for API responses

### API Endpoints Completed
- [x] POST `/tx` - Create transaction
- [x] GET `/tx` - List transactions with pagination
- [x] GET `/tx/stream` - SSE endpoint for real-time updates
- [x] POST `/cron/threshold` - Manual threshold check
- [x] GET `/roundup/orders/all` - Get all round-up orders
- [x] GET `/roundup/summary` - Get round-up summary statistics
- [x] GET `/roundup/coinbase/orders/{currency}` - Get Coinbase orders by currency
- [x] GET `/config/threshold` - Get current threshold configuration
- [x] PUT `/config/threshold` - Update threshold value
- [x] GET `/config/product` - Get active cryptocurrency product
- [x] PUT `/config/product` - Change cryptocurrency product (BTC/ETH)
- [x] GET `/config/all` - Get all configuration settings
- [x] GET `/api/diagnostic/ip` - Get server's outbound IP address

## Immediate Next Steps 🎯

### Quick Wins
- [ ] Add Ethereum price ticker alongside Bitcoin
- [ ] Implement transaction search by merchant name
- [ ] Add CSV export for transactions
- [ ] Create a dashboard summary widget
- [ ] Add sound effects for successful purchases
- [ ] Implement confetti animation on threshold reached

### UI Polish
- [ ] Add loading skeletons instead of spinners
- [ ] Implement smooth page transitions
- [ ] Add confirmation dialogs for critical actions
- [ ] Create onboarding tour for new users
- [ ] Add keyboard shortcuts (Ctrl+T for new transaction)
- [ ] Implement undo functionality for recent actions

## Known Issues 🐛

### ETH-USD Orders Failing
- **Issue**: Coinbase API returns 400 BAD_REQUEST for ETH-USD orders
- **Impact**: ETH orders fall back to simulated mode
- **Current behavior**: System continues to work using mock orders
- **Investigation needed**:
  - [ ] Check Coinbase sandbox documentation for supported products
  - [ ] Verify minimum order amounts for different products
  - [ ] Test with different order amounts ($10, $25, $50)
  - [ ] Check if account needs specific permissions for ETH trading

### Test Failures
- [ ] Fix `MultiProductTest.testListMultiProductOrders` - ETH order assertions
- [ ] Fix `FullFlowIntegrationTest` - Update to expect plain text response
- [ ] Update test configuration to handle Coinbase API unavailability gracefully

## High Priority Features 🚀

### Authentication & Security
- [ ] Implement JWT-based authentication
- [ ] Add Spring Security configuration
- [ ] Create user registration/login endpoints
- [ ] Implement role-based access control (RBAC)
- [ ] Add API rate limiting
- [ ] Secure Coinbase API credentials with encryption

### Multi-User Support
- [ ] Add User entity and relationships
- [ ] Associate transactions with users
- [ ] Implement user-specific spare change tracking
- [ ] Add user preferences for threshold and product
- [ ] Create user dashboard

### Production Database
- [ ] Migrate from H2 to PostgreSQL
- [ ] Update Flyway migrations for PostgreSQL
- [ ] Add database connection pooling
- [ ] Implement database backups
- [ ] Add indexes for performance

## Medium Priority Features 📋

### Enhanced Coinbase Integration
- [ ] Add retry logic with exponential backoff
- [ ] Implement webhook support for order status updates
- [ ] Add support for more cryptocurrencies (SOL, MATIC, ADA)
- [ ] Create order cancellation functionality
- [ ] Add real-time price tracking
- [ ] Implement limit orders in addition to market orders

### Frontend Enhancements
- [x] Add dark mode support (futuristic theme implemented)
- [x] Create detailed order history view (Crypto Orders tab)
- [ ] Add transaction search and filtering
- [ ] Implement charts for spare change trends
- [ ] Add portfolio value tracking
- [ ] Create settings page for user preferences
- [ ] Implement PWA features for mobile

### Monitoring & Observability
- [ ] Add Prometheus metrics
- [ ] Implement distributed tracing
- [ ] Create Grafana dashboards
- [ ] Add health check endpoints
- [ ] Implement audit logging
- [ ] Set up alerts for failed orders

## Low Priority Features 💡

### Advanced Features
- [ ] Scheduled/recurring purchases
- [ ] Bank account integration via Plaid
- [ ] Tax reporting functionality
- [ ] Export transactions to CSV/PDF
- [ ] Price alerts and notifications
- [ ] Social features (share achievements)
- [ ] Referral program

### Mobile Applications
- [ ] React Native mobile app
- [ ] Push notifications
- [ ] Biometric authentication
- [ ] Offline mode support

### Analytics
- [ ] Investment performance tracking
- [ ] ROI calculations
- [ ] Comparative analysis with market indices
- [ ] Predictive analytics for spare change

## Technical Debt 🔧

### Code Quality
- [ ] Increase test coverage to 80%+
- [ ] Add integration tests for all endpoints
- [ ] Implement contract testing
- [ ] Add static code analysis (SonarQube)
- [ ] Refactor large service classes
- [ ] Standardize error handling

### Documentation
- [ ] Complete API documentation with OpenAPI/Swagger
- [ ] Add JSDoc comments to all components
- [ ] Create architecture decision records (ADRs)
- [ ] Write deployment guide
- [ ] Add troubleshooting guide
- [ ] Create video tutorials

### Infrastructure
- [ ] Set up CI/CD pipeline with GitHub Actions
- [ ] Configure auto-scaling
- [ ] Implement blue-green deployments
- [ ] Add CDN for static assets
- [ ] Set up staging environment
- [ ] Implement secrets management (Vault)

## Performance Optimizations 🚀

- [ ] Implement Redis caching
- [ ] Add database query optimization
- [ ] Implement pagination for all list endpoints
- [ ] Add request/response compression
- [ ] Optimize Docker images
- [ ] Implement lazy loading in frontend

## Security Enhancements 🔒

- [ ] Add OWASP dependency scanning
- [ ] Implement Content Security Policy
- [ ] Add SQL injection prevention
- [ ] Implement request signing
- [ ] Add two-factor authentication
- [ ] Regular security audits

## Bug Fixes 🐞

- [ ] Fix WebSocket reconnection issues
- [ ] Handle timezone inconsistencies
- [ ] Fix memory leaks in SSE connections
- [ ] Resolve CORS issues in production
- [ ] Fix decimal precision in calculations

## Development Environment 🛠️

- [ ] Create development seed data
- [ ] Add hot reload for backend
- [ ] Improve local development setup
- [ ] Create makefile for common tasks
- [ ] Add pre-commit hooks
- [ ] Configure VSCode workspace settings

## Future Integrations 🔗

- [ ] Stripe for payment processing
- [ ] SendGrid for email notifications
- [ ] Twilio for SMS alerts
- [ ] Google Analytics for usage tracking
- [ ] Mixpanel for user behavior analytics
- [ ] Intercom for customer support

## Notes 📝

- Current focus should be on fixing ETH-USD orders and adding authentication
- Consider regulatory compliance (KYC/AML) for production
- Ensure PCI compliance if handling payment information
- Plan for GDPR compliance for EU users
- Consider implementing Circuit Breaker pattern for external APIs