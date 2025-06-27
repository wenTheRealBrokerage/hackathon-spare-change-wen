import { useState, useEffect } from 'react'
import { Card, Group, Text, Badge, Loader } from '@mantine/core'
import { IconCurrencyBitcoin, IconTrendingUp, IconTrendingDown } from '@tabler/icons-react'

function BitcoinPriceTicker() {
  const [btcPrice, setBtcPrice] = useState(null)
  const [priceChange, setPriceChange] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  const fetchBitcoinPrice = async () => {
    try {
      const response = await fetch('https://api.coinbase.com/v2/exchange-rates?currency=BTC')
      const data = await response.json()
      
      const newPrice = parseFloat(data.data.rates.USD)
      
      // Calculate price change
      if (btcPrice !== null) {
        setPriceChange(newPrice - btcPrice)
      }
      
      setBtcPrice(newPrice)
      setLoading(false)
      setError(false)
    } catch (err) {
      console.error('Failed to fetch Bitcoin price:', err)
      setError(true)
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchBitcoinPrice()
    const interval = setInterval(fetchBitcoinPrice, 30000) // Update every 30 seconds
    
    return () => clearInterval(interval)
  }, [])

  const cardStyle = {
    background: 'linear-gradient(135deg, rgba(247, 147, 26, 0.1) 0%, rgba(255, 200, 55, 0.1) 100%)',
    border: '1px solid rgba(247, 147, 26, 0.3)',
    transition: 'all 0.3s ease',
    backdropFilter: 'blur(10px)',
  }

  if (loading) {
    return (
      <Card p="lg" style={cardStyle}>
        <Group position="center">
          <Loader size="sm" color="orange" />
        </Group>
      </Card>
    )
  }

  if (error) {
    return (
      <Card p="lg" style={cardStyle}>
        <Text size="sm" c="dimmed">Unable to load BTC price</Text>
      </Card>
    )
  }

  const priceColor = priceChange === null ? 'gray' : priceChange > 0 ? 'green' : 'red'
  const PriceIcon = priceChange === null ? IconCurrencyBitcoin : priceChange > 0 ? IconTrendingUp : IconTrendingDown

  return (
    <Card p="lg" style={cardStyle}>
      <Group position="apart">
        <div>
          <Group spacing="xs" align="center">
            <IconCurrencyBitcoin size={20} style={{ color: '#F7931A' }} />
            <Text size="xs" transform="uppercase" weight={500} color="dimmed">
              Bitcoin Price
            </Text>
          </Group>
          <Text size="xl" weight={700} style={{ 
            color: '#F7931A',
            textShadow: '0 0 10px rgba(247, 147, 26, 0.5)' 
          }}>
            ${btcPrice?.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </Text>
          {priceChange !== null && (
            <Badge 
              color={priceColor} 
              variant="light" 
              size="sm"
              leftSection={<PriceIcon size={12} />}
            >
              {priceChange > 0 ? '+' : ''}{priceChange.toFixed(2)}
            </Badge>
          )}
        </div>
        <div style={{ 
          position: 'relative',
          width: 48,
          height: 48,
        }}>
          <IconCurrencyBitcoin 
            size={48} 
            style={{ 
              color: '#F7931A',
              opacity: 0.8,
              filter: 'drop-shadow(0 0 20px rgba(247, 147, 26, 0.5))',
              animation: 'rotate 20s linear infinite',
            }} 
          />
        </div>
      </Group>
      
      <style>{`
        @keyframes rotate {
          from {
            transform: rotate(0deg);
          }
          to {
            transform: rotate(360deg);
          }
        }
      `}</style>
    </Card>
  )
}

export default BitcoinPriceTicker