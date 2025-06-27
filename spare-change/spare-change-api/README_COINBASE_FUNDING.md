# Coinbase Sandbox Account Funding

## Current Situation
The Coinbase sandbox account currently has:
- 100,000 USDC
- 0 USD
- 0 BTC

## Issue
The `buyUsdcToBtc()` method attempts to buy BTC using the BTC-USD trading pair, which requires USD funds. Since the account only has USDC, the order fails with "Insufficient funds".

## Solutions

### Option 1: Fund the Account with USD
1. Log into Coinbase sandbox: https://public.sandbox.exchange.coinbase.com/
2. Navigate to your USD wallet
3. Use the deposit function to add fake USD to your account
4. The application will then work as expected

### Option 2: Implement USDC to USD Conversion
Convert USDC to USD first:
1. Buy LINK with USDC (LINK-USDC pair)
2. Sell LINK for USD (LINK-USD pair)
3. Buy BTC with USD (BTC-USD pair)

### Option 3: Direct USDC Trading (Not Available)
Unfortunately, there's no direct BTC-USDC trading pair in the sandbox.

## Recommendation
For demo purposes, Option 1 (funding the account with USD) is the simplest approach. The application is already configured correctly and will work once the account has USD funds.

## Testing the Implementation
Once the account has USD funds, you can test with:
```bash
# Run the integration test
mvn test -Dtest=CoinbaseClientIntegrationTest

# Or test the full flow
mvn test -Dtest=ThresholdServiceTest
```