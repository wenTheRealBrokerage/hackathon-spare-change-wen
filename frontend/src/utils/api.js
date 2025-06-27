const API_BASE = ''

export const api = {
  addRandomTx: async () => {
    const merchants = ['Amazon', 'Walmart', 'Target', 'BestBuy', 'Apple Store', 'Starbucks']
    const merchant = merchants[Math.floor(Math.random() * merchants.length)]
    const amountUsd = parseFloat((Math.random() * 100).toFixed(2))
    
    const response = await fetch(`${API_BASE}/tx`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ merchant, amountUsd }),
    })
    
    if (!response.ok) {
      throw new Error('Failed to add transaction')
    }
    
    return response.json()
  },
  
  addTransaction: async ({ merchant, amountUsd }) => {
    const response = await fetch(`${API_BASE}/tx`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ merchant, amountUsd: parseFloat(amountUsd) }),
    })
    
    if (!response.ok) {
      throw new Error('Failed to add transaction')
    }
    
    return response.json()
  },

  updateThreshold: async () => {
    const response = await fetch(`${API_BASE}/cron/threshold`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    })
    
    if (!response.ok) {
      throw new Error('Failed to check threshold')
    }
    
    // Check if response is JSON or plain text
    const contentType = response.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
      const data = await response.json()
      // Convert JSON response to user-friendly message
      if (data.executed === false) {
        return `Threshold not met yet. Keep adding transactions to accumulate more spare change!`
      } else if (data.executed === true) {
        return `Success! Your spare change has been invested in cryptocurrency.`
      }
      return data.message || 'Threshold check completed'
    } else {
      return response.text()
    }
  },

  getRoundupOrders: async () => {
    const response = await fetch(`${API_BASE}/roundup/orders/all`)
    
    if (!response.ok) {
      throw new Error('Failed to fetch roundup orders')
    }
    
    return response.json()
  },
  
  getCryptoOrdersSummary: async () => {
    const response = await fetch(`${API_BASE}/roundup/summary`)
    
    if (!response.ok) {
      throw new Error('Failed to fetch crypto orders summary')
    }
    
    return response.json()
  },
  
  getTransactions: async () => {
    const response = await fetch(`${API_BASE}/tx`)
    
    if (!response.ok) {
      throw new Error('Failed to fetch transactions')
    }
    
    return response.json()
  },
  
  getCoinbaseOrders: async () => {
    // First get the current product to determine which orders to fetch
    const productResponse = await fetch(`${API_BASE}/config/product`)
    if (!productResponse.ok) {
      throw new Error('Failed to fetch product configuration')
    }
    const productData = await productResponse.json()
    
    // Extract the currency from product ID (BTC-USD -> btc, ETH-USD -> eth)
    const currency = productData.currentProduct.split('-')[0].toLowerCase()
    
    const response = await fetch(`${API_BASE}/roundup/coinbase/orders/${currency}`)
    
    if (!response.ok) {
      throw new Error('Failed to fetch Coinbase orders')
    }
    
    return response.json()
  },
  
  getThreshold: async () => {
    const response = await fetch(`${API_BASE}/config/threshold`)
    
    if (!response.ok) {
      throw new Error('Failed to fetch threshold')
    }
    
    return response.json()
  },
  
  updateThresholdValue: async (threshold) => {
    const response = await fetch(`${API_BASE}/config/threshold`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ threshold: threshold.toString() }),
    })
    
    if (!response.ok) {
      throw new Error('Failed to update threshold')
    }
    
    return response.json()
  },
  
  getDiagnosticIp: async () => {
    const response = await fetch(`${API_BASE}/api/diagnostic/ip`)
    
    if (!response.ok) {
      throw new Error('Failed to fetch diagnostic IP')
    }
    
    return response.json()
  },
  
  getProduct: async () => {
    const response = await fetch(`${API_BASE}/config/product`)
    
    if (!response.ok) {
      throw new Error('Failed to fetch product configuration')
    }
    
    return response.json()
  },
  
  updateProduct: async (productId) => {
    const response = await fetch(`${API_BASE}/config/product`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ productId }),
    })
    
    if (!response.ok) {
      throw new Error('Failed to update product')
    }
    
    return response.json()
  },
}