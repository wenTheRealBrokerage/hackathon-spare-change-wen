import { Stack, Paper, Title, Table, Badge, Text, Loader, Center, Group, Card, Tabs, rem, Grid, Box } from '@mantine/core'
import { IconCoins, IconCoin, IconTrendingUp, IconWallet } from '@tabler/icons-react'
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
  
  const { data: productData } = useQuery({
    queryKey: ['product'],
    queryFn: api.getProduct,
    refetchInterval: 10000,
  })
  
  // Futuristic card style
  const cardStyle = {
    background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.1) 0%, rgba(106, 27, 255, 0.1) 100%)',
    border: '1px solid rgba(14, 196, 255, 0.3)',
    transition: 'all 0.3s ease',
    backdropFilter: 'blur(10px)',
  }

  if (ordersLoading || summaryLoading || coinbaseLoading) {
    return (
      <Center h={400}>
        <Stack align="center">
          <Loader size="lg" color="neon" />
          <Text size="sm" c="dimmed" transform="uppercase" style={{ letterSpacing: '1px' }}>
            Loading Crypto Data...
          </Text>
        </Stack>
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

  const iconStyle = { width: rem(16), height: rem(16) }

  return (
    <>
      <style>{`
        @keyframes pulse {
          0% {
            transform: scale(1);
          }
          50% {
            transform: scale(1.05);
          }
          100% {
            transform: scale(1);
          }
        }
      `}</style>
    <Stack gap="xl">
      {/* Stats Cards */}
      {summary && (
        <Grid>
          <Grid.Col span={{ base: 12, md: 6 }}>
            <Card 
              p="xl"
              style={{
                ...cardStyle,
                animation: 'pulse 2s infinite',
              }}
            >
              <Group position="apart">
                <div>
                  <Text size="xs" transform="uppercase" weight={500} color="dimmed" style={{ letterSpacing: '1px' }}>
                    Total Round-Up Orders
                  </Text>
                  <Text size="2.5rem" weight={700} style={{ 
                    textShadow: '0 0 20px rgba(14, 196, 255, 0.8)',
                    fontFamily: 'JetBrains Mono, monospace' 
                  }}>
                    {summary.totalOrders || 0}
                  </Text>
                </div>
                <IconCoins size={48} style={{ color: '#0EC4FF', opacity: 0.8 }} />
              </Group>
            </Card>
          </Grid.Col>
          
          <Grid.Col span={{ base: 12, md: 6 }}>
            <Card p="xl" style={cardStyle}>
              <Group position="apart">
                <div>
                  <Text size="xs" transform="uppercase" weight={500} color="dimmed" style={{ letterSpacing: '1px' }}>
                    Total USD Converted to {productData?.currentProduct === 'ETH-USD' ? 'ETH' : 'BTC'}
                  </Text>
                  <Text size="2.5rem" weight={700} style={{ 
                    textShadow: '0 0 20px rgba(14, 196, 255, 0.8)',
                    fontFamily: 'JetBrains Mono, monospace' 
                  }}>
                    ${summary.totalUsdConverted?.toFixed(2) || '0.00'}
                  </Text>
                </div>
                <IconTrendingUp size={48} style={{ color: '#6A1BFF', opacity: 0.8 }} />
              </Group>
            </Card>
          </Grid.Col>
        </Grid>
      )}
      
      <Tabs 
        defaultValue="roundup"
        styles={{
          tab: {
            fontSize: '0.875rem',
            fontWeight: 500,
            letterSpacing: '0.5px',
            textTransform: 'uppercase',
            transition: 'all 0.3s ease',
            '&[data-active]': {
              color: '#0EC4FF',
              borderBottomColor: '#0EC4FF',
            },
          },
        }}
      >
        <Tabs.List>
          <Tabs.Tab value="roundup" leftSection={<IconWallet style={iconStyle} />}>
            Round-Up Orders
          </Tabs.Tab>
          <Tabs.Tab value="coinbase" leftSection={<IconCoin style={iconStyle} />}>
            Coinbase {productData?.currentProduct === 'ETH-USD' ? 'ETH' : 'BTC'} Orders
          </Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="roundup" pt="xl">
          <Paper p="xl" radius="md" style={cardStyle}>
            <Title order={4} mb="lg">Round-Up History</Title>
            <Table 
              striped 
              highlightOnHover
              styles={{
                thead: {
                  backgroundColor: 'rgba(14, 196, 255, 0.05)',
                },
                th: {
                  textTransform: 'uppercase',
                  fontSize: '0.75rem',
                  letterSpacing: '1px',
                  fontWeight: 600,
                  color: '#0EC4FF',
                },
              }}
            >
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
                  <Table.Tr key={order.id} style={{ transition: 'all 0.2s ease' }}>
                    <Table.Td>
                      <Text style={{ fontFamily: 'JetBrains Mono' }}>
                        #{order.id.toString().padStart(4, '0')}
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Badge size="lg" variant="light" color="neon">
                        ${order.totalUsd.toFixed(2)}
                      </Badge>
                    </Table.Td>
                    <Table.Td>
                      <Text size="sm" style={{ fontFamily: 'JetBrains Mono', color: '#6A1BFF' }}>
                        {order.coinbaseOrderId || 'N/A'}
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Text size="sm" c="dimmed">
                        {new Date(order.createdAt).toLocaleString()}
                      </Text>
                    </Table.Td>
                  </Table.Tr>
                ))}
                {roundupOrders.length === 0 && (
                  <Table.Tr>
                    <Table.Td colSpan={4} style={{ textAlign: 'center', padding: '2rem' }}>
                      <Text c="dimmed" size="sm">
                        No round-up orders yet. Orders appear when spare change reaches the threshold.
                      </Text>
                    </Table.Td>
                  </Table.Tr>
                )}
              </Table.Tbody>
            </Table>
          </Paper>
        </Tabs.Panel>

        <Tabs.Panel value="coinbase" pt="xl">
          <Paper p="xl" radius="md" style={cardStyle}>
            <Title order={4} mb="lg">Live Coinbase Orders</Title>
            <Table 
              striped 
              highlightOnHover
              styles={{
                thead: {
                  backgroundColor: 'rgba(14, 196, 255, 0.05)',
                },
                th: {
                  textTransform: 'uppercase',
                  fontSize: '0.75rem',
                  letterSpacing: '1px',
                  fontWeight: 600,
                  color: '#0EC4FF',
                },
              }}
            >
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>Order ID</Table.Th>
                  <Table.Th>Type</Table.Th>
                  <Table.Th>Side</Table.Th>
                  <Table.Th>Status</Table.Th>
                  <Table.Th>Size</Table.Th>
                  <Table.Th>Value</Table.Th>
                  <Table.Th>Created</Table.Th>
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {coinbaseOrders.map((order) => (
                  <Table.Tr key={order.id} style={{ transition: 'all 0.2s ease' }}>
                    <Table.Td>
                      <Text size="sm" style={{ fontFamily: 'JetBrains Mono' }}>
                        {order.id.substring(0, 8)}...
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Badge variant="outline" color="cyber">
                        {order.type}
                      </Badge>
                    </Table.Td>
                    <Table.Td>
                      <Badge 
                        variant="dot"
                        color={order.side === 'buy' ? 'green' : 'red'}
                      >
                        {order.side.toUpperCase()}
                      </Badge>
                    </Table.Td>
                    <Table.Td>
                      <Badge 
                        variant="light" 
                        color={
                          order.status === 'done' ? 'green' : 
                          order.status === 'pending' ? 'yellow' : 'gray'
                        }
                      >
                        {order.status}
                      </Badge>
                    </Table.Td>
                    <Table.Td>
                      <Text size="sm" style={{ fontFamily: 'JetBrains Mono' }}>
                        {order.size || order.filledSize || '-'}
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Text size="sm" weight={500}>
                        ${order.executedValue || order.funds || '-'}
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Text size="sm" c="dimmed">
                        {new Date(order.createdAt).toLocaleString()}
                      </Text>
                    </Table.Td>
                  </Table.Tr>
                ))}
                {coinbaseOrders.length === 0 && (
                  <Table.Tr>
                    <Table.Td colSpan={7} style={{ textAlign: 'center', padding: '2rem' }}>
                      <Text c="dimmed" size="sm">
                        No Coinbase BTC orders found.
                      </Text>
                    </Table.Td>
                  </Table.Tr>
                )}
              </Table.Tbody>
            </Table>
          </Paper>
        </Tabs.Panel>
      </Tabs>
    </Stack>
    </>
  )
}

export default CryptoOrdersTab