import { Title, Text, Box, Group, Container, Stack } from '@mantine/core'
import { IconCurrencyBitcoin } from '@tabler/icons-react'
import CryptoPriceTicker from './CryptoPriceTicker'

function FuturisticHeader() {
  return (
    <Box
      style={{
        background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.1) 0%, rgba(106, 27, 255, 0.1) 100%)',
        backdropFilter: 'blur(10px)',
        borderBottom: '1px solid rgba(14, 196, 255, 0.3)',
        padding: '2rem 0',
        marginBottom: '2rem',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      <Box
        style={{
          position: 'absolute',
          top: 0,
          left: '-100%',
          width: '200%',
          height: '100%',
          background: 'linear-gradient(90deg, transparent, rgba(14, 196, 255, 0.4), transparent)',
          animation: 'scan 8s linear infinite',
        }}
      />
      
      <Container size="xl">
        <Group position="apart" align="center">
          <Group align="center" spacing="xl">
            <IconCurrencyBitcoin 
              size={48} 
              style={{ 
                color: '#0EC4FF',
                filter: 'drop-shadow(0 0 10px rgba(14, 196, 255, 0.8))',
              }} 
            />
            <div>
              <Title 
                order={1} 
                style={{
                  fontSize: '2.5rem',
                  fontWeight: 700,
                  letterSpacing: '-0.5px',
                  marginBottom: '0.25rem',
                }}
              >
                <span className="gradient-text">SPARE CHANGE</span>
              </Title>
              <Text 
                size="sm" 
                style={{ 
                  color: '#0EC4FF',
                  textTransform: 'uppercase',
                  letterSpacing: '2px',
                  fontWeight: 500,
                }}
              >
                Automated Cryptocurrency Investment Platform
              </Text>
            </div>
          </Group>
          
          <Group gap="md">
            <CryptoPriceTicker currency="BTC" />
            <CryptoPriceTicker currency="ETH" />
          </Group>
        </Group>
      </Container>
      
      <style>{`
        @keyframes scan {
          0% {
            transform: translateX(-100%);
          }
          100% {
            transform: translateX(100%);
          }
        }
      `}</style>
    </Box>
  )
}

export default FuturisticHeader