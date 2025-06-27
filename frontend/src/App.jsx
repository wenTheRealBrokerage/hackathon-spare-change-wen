import { useState } from 'react'
import { Container, Tabs, rem } from '@mantine/core'
import { IconCoin, IconList } from '@tabler/icons-react'
import TransactionTab from './components/TransactionTab'
import CryptoOrdersTab from './components/CryptoOrdersTab'

function App() {
  const iconStyle = { width: rem(12), height: rem(12) }

  return (
    <Container size="lg" py="md">
      <Tabs defaultValue="transactions">
        <Tabs.List>
          <Tabs.Tab value="transactions" leftSection={<IconList style={iconStyle} />}>
            Transactions
          </Tabs.Tab>
          <Tabs.Tab value="crypto" leftSection={<IconCoin style={iconStyle} />}>
            Crypto Orders
          </Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="transactions" pt="xs">
          <TransactionTab />
        </Tabs.Panel>

        <Tabs.Panel value="crypto" pt="xs">
          <CryptoOrdersTab />
        </Tabs.Panel>
      </Tabs>
    </Container>
  )
}

export default App