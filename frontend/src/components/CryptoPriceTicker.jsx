import { useState, useEffect } from 'react'
import { Card, Group, Text, Badge, Loader, Tooltip } from '@mantine/core'
import { IconCurrencyBitcoin, IconCurrencyEthereum, IconTrendingUp, IconTrendingDown } from '@tabler/icons-react'

function CryptoPriceTicker({ currency = 'BTC' }) {
  const [price, setPrice] = useState(null)
  const [priceChange, setPriceChange] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  const isETH = currency === 'ETH'
  const cryptoName = isETH ? 'Ethereum' : 'Bitcoin'
  const cryptoColor = isETH ? '#627EEA' : '#F7931A'
  const CryptoIcon = isETH ? IconCurrencyEthereum : IconCurrencyBitcoin

  const fetchPrice = async () => {
    try {
      const response = await fetch(`https://api.coinbase.com/v2/exchange-rates?currency=${currency}`)
      const data = await response.json()
      
      const newPrice = parseFloat(data.data.rates.USD)
      
      // Calculate price change
      if (price !== null) {
        setPriceChange(newPrice - price)
      }
      
      setPrice(newPrice)
      setLoading(false)
      setError(false)
    } catch (err) {
      console.error(`Failed to fetch ${cryptoName} price:`, err)
      setError(true)
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPrice()
    const interval = setInterval(fetchPrice, 30000) // Update every 30 seconds
    
    return () => clearInterval(interval)
  }, [currency])

  const cardStyle = {
    background: isETH 
      ? 'linear-gradient(135deg, rgba(98, 126, 234, 0.1) 0%, rgba(134, 156, 255, 0.1) 100%)'
      : 'linear-gradient(135deg, rgba(247, 147, 26, 0.1) 0%, rgba(255, 200, 55, 0.1) 100%)',
    border: `1px solid ${isETH ? 'rgba(98, 126, 234, 0.3)' : 'rgba(247, 147, 26, 0.3)'}`,
    transition: 'all 0.3s ease',
    backdropFilter: 'blur(10px)',
  }

  if (loading) {
    return (
      <Card p="lg" style={cardStyle}>
        <Group position="center">
          <Loader size="sm" color={isETH ? 'indigo' : 'orange'} />
        </Group>
      </Card>
    )
  }

  if (error) {
    return (
      <Card p="lg" style={cardStyle}>
        <Text size="sm" c="dimmed">Unable to load {currency} price</Text>
      </Card>
    )
  }

  const priceColor = priceChange === null ? 'gray' : priceChange > 0 ? 'green' : 'red'
  const PriceIcon = priceChange === null ? CryptoIcon : priceChange > 0 ? IconTrendingUp : IconTrendingDown

  return (
    <Tooltip label="Price updates every 30 seconds from Coinbase API" position="bottom" withArrow>
      <Card p="lg" style={{ ...cardStyle, cursor: 'help' }}>
        <Group position="apart">
          <div>
            <Group spacing="xs" align="center">
              <CryptoIcon size={20} style={{ color: cryptoColor }} />
              <Text size="xs" transform="uppercase" weight={500} color="dimmed">
                {cryptoName} Price
              </Text>
            </Group>
            <Text size="xl" weight={700} style={{ 
              color: cryptoColor,
              textShadow: `0 0 10px ${isETH ? 'rgba(98, 126, 234, 0.5)' : 'rgba(247, 147, 26, 0.5)'}` 
            }}>
              ${price?.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
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
          <CryptoIcon 
            size={48} 
            style={{ 
              color: cryptoColor,
              opacity: 0.8,
              filter: `drop-shadow(0 0 20px ${isETH ? 'rgba(98, 126, 234, 0.5)' : 'rgba(247, 147, 26, 0.5)'})`,
              animation: isETH ? 'pulse 2s ease-in-out infinite' : 'rotate 20s linear infinite',
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
        @keyframes pulse {
          0% {
            transform: scale(1);
            opacity: 0.8;
          }
          50% {
            transform: scale(1.1);
            opacity: 1;
          }
          100% {
            transform: scale(1);
            opacity: 0.8;
          }
        }
      `}</style>
      </Card>
    </Tooltip>
  )
}

export default CryptoPriceTicker