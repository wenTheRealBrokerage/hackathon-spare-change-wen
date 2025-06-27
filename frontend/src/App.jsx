import { useState } from 'react'
import { Container, Tabs, rem, Box } from '@mantine/core'
import { IconCoin, IconList } from '@tabler/icons-react'
import TransactionTab from './components/TransactionTab'
import CryptoOrdersTab from './components/CryptoOrdersTab'
import FuturisticHeader from './components/FuturisticHeader'

function App() {
  const iconStyle = { width: rem(16), height: rem(16) }

  return (
    <Box style={{ minHeight: '100vh' }}>
      <FuturisticHeader />
      <Container size="xl" pb="xl">
        <Tabs 
          defaultValue="transactions"
          styles={{
            root: {
              backgroundColor: 'transparent',
            },
            list: {
              borderBottom: '2px solid rgba(14, 196, 255, 0.2)',
              marginBottom: '1.5rem',
            },
            tab: {
              color: '#A6A7AB',
              fontSize: '0.875rem',
              fontWeight: 500,
              letterSpacing: '0.5px',
              textTransform: 'uppercase',
              transition: 'all 0.3s ease',
              borderBottom: '2px solid transparent',
              marginBottom: '-2px',
              '&[data-active]': {
                color: '#0EC4FF',
                borderBottomColor: '#0EC4FF',
                backgroundColor: 'transparent',
              },
              '&:hover': {
                backgroundColor: 'rgba(14, 196, 255, 0.05)',
                borderBottomColor: 'rgba(14, 196, 255, 0.3)',
              },
            },
          }}
        >
          <Tabs.List>
            <Tabs.Tab value="transactions" leftSection={<IconList style={iconStyle} />}>
              Transactions
            </Tabs.Tab>
            <Tabs.Tab value="crypto" leftSection={<IconCoin style={iconStyle} />}>
              Crypto Orders
            </Tabs.Tab>
          </Tabs.List>

          <Tabs.Panel value="transactions">
            <TransactionTab />
          </Tabs.Panel>

          <Tabs.Panel value="crypto">
            <CryptoOrdersTab />
          </Tabs.Panel>
        </Tabs>
      </Container>
    </Box>
  )
}

export default App