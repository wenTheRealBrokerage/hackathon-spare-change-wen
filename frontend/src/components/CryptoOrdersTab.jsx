import { Stack, Paper, Title, Table, Badge, Text, Loader, Center, Group, Card, Tabs, rem } from '@mantine/core'
import { IconCoins, IconCoin } from '@tabler/icons-react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../utils/api'

function CryptoOrdersTab() {
  const { data: roundupOrders = [], isLoading: ordersLoading, error: ordersError } = useQuery({
    queryKey: ['roundupOrders'],
    queryFn: api.getRoundupOrders,
    refetchInterval: 3000,
  })

  const { data: summary, isLoading: summaryLoading } = useQuery({
    queryKey: ['cryptoOrdersSummary'],
    queryFn: api.getCryptoOrdersSummary,
    refetchInterval: 3000,
  })
  
  const { data: coinbaseOrders = [], isLoading: coinbaseLoading } = useQuery({
    queryKey: ['coinbaseOrders'],
    queryFn: api.getCoinbaseOrders,
    refetchInterval: 3000,
  })

  if (ordersLoading || summaryLoading || coinbaseLoading) {
    return (
      <Center h={200}>
        <Loader />
      </Center>
    )
  }

  if (ordersError) {
    return (
      <Center h={200}>
        <Text c="red">Failed to load crypto orders</Text>
      </Center>
    )
  }

  const iconStyle = { width: rem(12), height: rem(12) }

  return (
    <Stack gap="md">
      <Title order={3}>Crypto Orders</Title>
      
      {summary && (
        <Group grow>
          <Card shadow="sm" padding="lg" radius="md" withBorder>
            <Text size="sm" c="dimmed">Total Round-Up Orders</Text>
            <Text size="xl" fw={700}>{summary.totalOrders || 0}</Text>
          </Card>
          <Card shadow="sm" padding="lg" radius="md" withBorder>
            <Text size="sm" c="dimmed">Total USD Converted</Text>
            <Text size="xl" fw={700}>${summary.totalUsdConverted?.toFixed(2) || '0.00'}</Text>
          </Card>
        </Group>
      )}
      
      <Tabs defaultValue="roundup">
        <Tabs.List>
          <Tabs.Tab value="roundup" leftSection={<IconCoins style={iconStyle} />}>
            Round-Up Orders
          </Tabs.Tab>
          <Tabs.Tab value="coinbase" leftSection={<IconCoin style={iconStyle} />}>
            Coinbase BTC Orders
          </Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="roundup" pt="xs">
          <Paper shadow="xs" p="md" mt="sm">
            <Table striped highlightOnHover>
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>Order ID</Table.Th>
                  <Table.Th>Amount (USD)</Table.Th>
                  <Table.Th>Coinbase Order ID</Table.Th>
                  <Table.Th>Created</Table.Th>
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {roundupOrders.map((order) => (
                  <Table.Tr key={order.id}>
                    <Table.Td>{order.id}</Table.Td>
                    <Table.Td>${order.totalUsd.toFixed(2)}</Table.Td>
                    <Table.Td>
                      <Text size="sm" style={{ fontFamily: 'monospace' }}>
                        {order.coinbaseOrderId || 'N/A'}
                      </Text>
                    </Table.Td>
                    <Table.Td>{new Date(order.createdAt).toLocaleString()}</Table.Td>
                  </Table.Tr>
                ))}
                {roundupOrders.length === 0 && (
                  <Table.Tr>
                    <Table.Td colSpan={4} style={{ textAlign: 'center' }}>
                      <Text c="dimmed">No round-up orders yet. Orders appear when spare change reaches $5.</Text>
                    </Table.Td>
                  </Table.Tr>
                )}
              </Table.Tbody>
            </Table>
          </Paper>
        </Tabs.Panel>

        <Tabs.Panel value="coinbase" pt="xs">
          <Paper shadow="xs" p="md" mt="sm">
            <Table striped highlightOnHover>
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>Order ID</Table.Th>
                  <Table.Th>Type</Table.Th>
                  <Table.Th>Side</Table.Th>
                  <Table.Th>Status</Table.Th>
                  <Table.Th>Size</Table.Th>
                  <Table.Th>Executed Value</Table.Th>
                  <Table.Th>Created</Table.Th>
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {coinbaseOrders.map((order) => (
                  <Table.Tr key={order.id}>
                    <Table.Td>
                      <Text size="sm" style={{ fontFamily: 'monospace' }}>
                        {order.id.substring(0, 8)}...
                      </Text>
                    </Table.Td>
                    <Table.Td>{order.type}</Table.Td>
                    <Table.Td>
                      <Badge color={order.side === 'buy' ? 'green' : 'red'}>
                        {order.side}
                      </Badge>
                    </Table.Td>
                    <Table.Td>
                      <Badge variant="light" color={
                        order.status === 'done' ? 'green' : 
                        order.status === 'pending' ? 'yellow' : 'gray'
                      }>
                        {order.status}
                      </Badge>
                    </Table.Td>
                    <Table.Td>{order.size || order.filledSize || '-'}</Table.Td>
                    <Table.Td>${order.executedValue || order.funds || '-'}</Table.Td>
                    <Table.Td>{new Date(order.createdAt).toLocaleString()}</Table.Td>
                  </Table.Tr>
                ))}
                {coinbaseOrders.length === 0 && (
                  <Table.Tr>
                    <Table.Td colSpan={7} style={{ textAlign: 'center' }}>
                      <Text c="dimmed">No Coinbase BTC orders found.</Text>
                    </Table.Td>
                  </Table.Tr>
                )}
              </Table.Tbody>
            </Table>
          </Paper>
        </Tabs.Panel>
      </Tabs>
    </Stack>
  )
}

export default CryptoOrdersTab