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
    
    return response.text()
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
    const response = await fetch(`${API_BASE}/roundup/coinbase/orders/btc`)
    
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
}