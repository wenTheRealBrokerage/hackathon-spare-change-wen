# Coinbase Exchange (Advanced Trade) API Setup Guide

## Important: Coinbase has Two Different APIs

1. **Coinbase API** (api.coinbase.com) - For retail users
2. **Coinbase Exchange API** (api.exchange.coinbase.com) - For trading

This application uses the **Coinbase Exchange API**.

## Getting Sandbox API Keys

1. Go to: https://public.sandbox.exchange.coinbase.com/
2. Sign up for a sandbox account (separate from your main Coinbase account)
3. Once logged in, go to Settings → API
4. Click "New API Key"
5. Select permissions:
   - View (for reading orders)
   - Trade (for placing orders)
6. **IMPORTANT**: Save the passphrase shown! It's only displayed once.
7. Save your:
   - API Key
   - API Secret (base64 encoded string)
   - Passphrase (exactly as shown, case-sensitive)

## Common Issues

### "Invalid Passphrase" Error
- Make sure you're using Exchange API keys, not regular Coinbase API keys
- The passphrase is case-sensitive
- Check for trailing spaces or special characters
- Ensure you're using the sandbox URL: https://api-public.sandbox.exchange.coinbase.com

### Testing Your Credentials

Run this test to verify your setup:
```bash
mvn test -Dtest=CoinbaseClientIntegrationTest -pl spare-change-api
```

## Sandbox Limitations

- Orders are simulated, no real money is involved
- You need to fund your sandbox account with fake USD/BTC
- Market data may not reflect real prices

## Update Your Credentials

Edit `application.yaml`:
```yaml
coinbase:
  api:
    key: YOUR_SANDBOX_API_KEY
    secret: YOUR_SANDBOX_API_SECRET
    passphrase: YOUR_SANDBOX_PASSPHRASE
```

Or use environment variables:
```bash
export COINBASE_API_KEY=your_key
export COINBASE_API_SECRET=your_secret
export COINBASE_API_PASSPHRASE=your_passphrase
```