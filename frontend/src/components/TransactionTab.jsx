import { useState } from 'react'
import { Button, Stack, Group, Paper, Text, Badge, Table, ActionIcon, Title, NumberInput, Card, TextInput, Modal, Box, Grid, Progress } from '@mantine/core'
import { notifications } from '@mantine/notifications'
import { useMutation, useQueryClient, useQuery } from '@tanstack/react-query'
import { IconPlus, IconMinus, IconEdit, IconShoppingCart, IconNetwork, IconRocket, IconBolt, IconWallet } from '@tabler/icons-react'
import { api } from '../utils/api'
import { useTransactionStream } from '../hooks/useTransactionStream'

function TransactionTab() {
  const [pageSize, setPageSize] = useState(5)
  const [editingThreshold, setEditingThreshold] = useState(false)
  const [newThreshold, setNewThreshold] = useState(5)
  const [manualTxModalOpen, setManualTxModalOpen] = useState(false)
  const [manualTxData, setManualTxData] = useState({ merchant: '', amountUsd: '' })
  const { transactions, isConnected } = useTransactionStream()
  const queryClient = useQueryClient()
  
  // Futuristic card style
  const cardStyle = {
    background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.1) 0%, rgba(106, 27, 255, 0.1) 100%)',
    border: '1px solid rgba(14, 196, 255, 0.3)',
    transition: 'all 0.3s ease',
    backdropFilter: 'blur(10px)',
  }
  
  const { data: thresholdData, isLoading: thresholdLoading } = useQuery({
    queryKey: ['threshold'],
    queryFn: api.getThreshold,
    refetchInterval: 10000,
  })

  const addTxMutation = useMutation({
    mutationFn: api.addRandomTx,
    onSuccess: () => {
      notifications.show({
        title: 'Success',
        message: 'Random transaction added',
        color: 'green',
      })
    },
    onError: () => {
      notifications.show({
        title: 'Error',
        message: 'Failed to add transaction',
        color: 'red',
      })
    },
  })
  
  const addManualTxMutation = useMutation({
    mutationFn: api.addTransaction,
    onSuccess: () => {
      notifications.show({
        title: 'Success',
        message: 'Transaction added successfully',
        color: 'green',
      })
      setManualTxModalOpen(false)
      setManualTxData({ merchant: '', amountUsd: '' })
    },
    onError: () => {
      notifications.show({
        title: 'Error',
        message: 'Failed to add transaction',
        color: 'red',
      })
    },
  })

  const thresholdMutation = useMutation({
    mutationFn: api.updateThreshold,
    onSuccess: (data) => {
      notifications.show({
        title: 'Spare Change Check',
        message: data,
        color: 'blue',
      })
      // Invalidate the transactions query to force a refresh
      setTimeout(() => {
        queryClient.invalidateQueries({ queryKey: ['transactions'] })
      }, 1000)
    },
    onError: () => {
      notifications.show({
        title: 'Error',
        message: 'Failed to check spare change threshold',
        color: 'red',
      })
    },
  })
  
  const updateThresholdMutation = useMutation({
    mutationFn: api.updateThresholdValue,
    onSuccess: (data) => {
      notifications.show({
        title: 'Threshold Updated',
        message: `Threshold changed from $${data.previousThreshold} to $${data.newThreshold}`,
        color: 'green',
      })
      setEditingThreshold(false)
      queryClient.invalidateQueries({ queryKey: ['threshold'] })
    },
    onError: () => {
      notifications.show({
        title: 'Error',
        message: 'Failed to update threshold',
        color: 'red',
      })
    },
  })
  
  const diagnosticMutation = useMutation({
    mutationFn: api.getDiagnosticIp,
    onSuccess: (data) => {
      notifications.show({
        title: 'Server Outbound IP',
        message: `${data.outboundIp} - ${data.message}`,
        color: 'blue',
        autoClose: 10000, // Keep notification visible longer
      })
    },
    onError: () => {
      notifications.show({
        title: 'Error',
        message: 'Failed to get diagnostic information',
        color: 'red',
      })
    },
  })

  const displayedTransactions = transactions.slice(0, pageSize)
  
  // Calculate total spare change for NEW transactions
  const totalSpareChange = transactions
    .filter(tx => tx.status === 'NEW')
    .reduce((sum, tx) => sum + (tx.spareUsd || 0), 0)

  return (
    <>
      <style>{`
        @keyframes pulse {
          0% {
            box-shadow: 0 0 0 0 rgba(14, 196, 255, 0.7);
          }
          70% {
            box-shadow: 0 0 0 10px rgba(14, 196, 255, 0);
          }
          100% {
            box-shadow: 0 0 0 0 rgba(14, 196, 255, 0);
          }
        }
      `}</style>
    <Stack gap="xl">
      {/* Stats Cards */}
      <Grid>
        <Grid.Col span={{ base: 12, sm: 6, md: 3 }}>
          <Card 
            p="lg"
            style={{
              background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.1) 0%, rgba(106, 27, 255, 0.1) 100%)',
              border: '1px solid rgba(14, 196, 255, 0.3)',
              transition: 'all 0.3s ease',
            }}
          >
            <Group position="apart">
              <div>
                <Text size="xs" transform="uppercase" weight={500} color="dimmed">
                  Total Spare Change
                </Text>
                <Text size="xl" weight={700} style={{ textShadow: '0 0 10px rgba(14, 196, 255, 0.8)' }}>
                  ${totalSpareChange.toFixed(2)}
                </Text>
              </div>
              <IconWallet size={32} style={{ color: '#0EC4FF', opacity: 0.8 }} />
            </Group>
            <Progress 
              value={(totalSpareChange / (thresholdData?.currentThreshold || 5)) * 100} 
              color="neon" 
              size="xs" 
              mt="sm"
              styles={{
                bar: {
                  backgroundImage: 'linear-gradient(90deg, #0EC4FF 0%, #6A1BFF 100%)',
                }
              }}
            />
          </Card>
        </Grid.Col>
        
        <Grid.Col span={{ base: 12, sm: 6, md: 3 }}>
          <Card p="lg" style={cardStyle}>
            <Group position="apart">
              <div>
                <Text size="xs" transform="uppercase" weight={500} color="dimmed">
                  Transactions
                </Text>
                <Text size="xl" weight={700}>
                  {transactions.length}
                </Text>
              </div>
              <IconBolt size={32} style={{ color: '#6A1BFF', opacity: 0.8 }} />
            </Group>
          </Card>
        </Grid.Col>
        
        <Grid.Col span={{ base: 12, sm: 6, md: 3 }}>
          <Card p="lg" style={cardStyle}>
            <Group position="apart">
              <div>
                <Text size="xs" transform="uppercase" weight={500} color="dimmed">
                  Threshold
                </Text>
                {editingThreshold ? (
                  <Group gap="xs">
                    <NumberInput
                      value={newThreshold}
                      onChange={setNewThreshold}
                      min={0.01}
                      max={1000}
                      step={0.5}
                      precision={2}
                      prefix="$"
                      size="xs"
                      style={{ width: 80 }}
                    />
                    <ActionIcon size="sm" color="green" onClick={() => updateThresholdMutation.mutate(newThreshold)}>
                      ✓
                    </ActionIcon>
                    <ActionIcon size="sm" color="red" onClick={() => {
                      setEditingThreshold(false)
                      setNewThreshold(thresholdData?.currentThreshold || 5)
                    }}>
                      ✕
                    </ActionIcon>
                  </Group>
                ) : (
                  <Group gap="xs" align="center">
                    <Text size="xl" weight={700}>
                      ${thresholdData?.currentThreshold || '5.00'}
                    </Text>
                    <ActionIcon 
                      size="sm" 
                      variant="subtle"
                      color="neon"
                      onClick={() => {
                        setEditingThreshold(true)
                        setNewThreshold(thresholdData?.currentThreshold || 5)
                      }}
                    >
                      <IconEdit size={14} />
                    </ActionIcon>
                  </Group>
                )}
              </div>
              <IconRocket size={32} style={{ color: '#0EC4FF', opacity: 0.8 }} />
            </Group>
          </Card>
        </Grid.Col>
        
        <Grid.Col span={{ base: 12, sm: 6, md: 3 }}>
          <Card 
            p="lg"
            style={{
              ...cardStyle,
              animation: isConnected ? 'pulse 2s infinite' : 'none',
            }}
          >
            <Group position="apart">
              <div>
                <Text size="xs" transform="uppercase" weight={500} color="dimmed">
                  Status
                </Text>
                <Badge 
                  size="lg" 
                  variant="dot" 
                  color={isConnected ? 'green' : 'red'}
                >
                  {isConnected ? 'LIVE' : 'OFFLINE'}
                </Badge>
              </div>
              <IconNetwork size={32} style={{ color: isConnected ? '#4ADE80' : '#EF4444', opacity: 0.8 }} />
            </Group>
          </Card>
        </Grid.Col>
      </Grid>

      {/* Action Buttons */}
      <Paper p="md" radius="md" style={cardStyle}>
        <Group justify="space-between" align="center">
          <Title order={4}>Transaction Controls</Title>
          <Group>
            <Button 
              onClick={() => addTxMutation.mutate()}
              loading={addTxMutation.isPending}
              size="md"
              styles={{
                root: {
                  background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.2) 0%, rgba(106, 27, 255, 0.2) 100%)',
                  border: '1px solid rgba(14, 196, 255, 0.5)',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'translateY(-2px)',
                    boxShadow: '0 5px 15px rgba(14, 196, 255, 0.4)',
                  }
                }
              }}
            >
              <IconBolt size={16} style={{ marginRight: 8 }} />
              RANDOM TX
            </Button>
            <Button 
              leftSection={<IconShoppingCart size={16} />}
              onClick={() => setManualTxModalOpen(true)}
              variant="default"
              size="md"
              styles={{
                root: {
                  borderColor: 'rgba(106, 27, 255, 0.5)',
                  '&:hover': {
                    borderColor: '#6A1BFF',
                  }
                }
              }}
            >
              MANUAL TX
            </Button>
            <Button 
              onClick={() => thresholdMutation.mutate()}
              loading={thresholdMutation.isPending}
              size="md"
              color="neon"
              variant="light"
            >
              <IconRocket size={16} style={{ marginRight: 8 }} />
              CHECK THRESHOLD
            </Button>
            <ActionIcon 
              size="lg"
              variant="subtle"
              onClick={() => diagnosticMutation.mutate()}
              loading={diagnosticMutation.isPending}
              color="neon"
            >
              <IconNetwork size={20} />
            </ActionIcon>
          </Group>
        </Group>
      </Paper>

      {/* Transactions Table */}
      <Paper p="xl" radius="md" style={cardStyle}>
        <Group justify="space-between" mb="lg">
          <div>
            <Title order={4}>Transaction History</Title>
            <Text size="sm" c="dimmed" mt="xs">
              Showing {displayedTransactions.length} of {transactions.length} transactions
            </Text>
          </div>
          <Group gap="xs">
            <ActionIcon 
              variant="light"
              color="neon"
              onClick={() => setPageSize(Math.max(1, pageSize - 1))}
              disabled={pageSize <= 1}
            >
              <IconMinus size={16} />
            </ActionIcon>
            <Badge size="lg" variant="outline" color="neon">
              {pageSize} rows
            </Badge>
            <ActionIcon 
              variant="light"
              color="neon"
              onClick={() => setPageSize(pageSize + 1)}
            >
              <IconPlus size={16} />
            </ActionIcon>
          </Group>
        </Group>

        <Table 
          striped 
          highlightOnHover
          styles={{
            table: {
              borderCollapse: 'separate',
              borderSpacing: '0 8px',
            },
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
              <Table.Th>ID</Table.Th>
              <Table.Th>Merchant</Table.Th>
              <Table.Th>Amount</Table.Th>
              <Table.Th>Spare Change</Table.Th>
              <Table.Th>Status</Table.Th>
              <Table.Th>Timestamp</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {displayedTransactions.map((tx) => (
              <Table.Tr 
                key={tx.id} 
                style={{
                  transition: 'all 0.2s ease',
                  cursor: 'pointer',
                }}
              >
                <Table.Td>
                  <Text style={{ fontFamily: 'JetBrains Mono', fontSize: '0.875rem' }}>
                    #{tx.id.toString().padStart(4, '0')}
                  </Text>
                </Table.Td>
                <Table.Td>
                  <Text weight={500}>{tx.merchant}</Text>
                </Table.Td>
                <Table.Td>
                  <Text style={{ fontFamily: 'JetBrains Mono' }}>
                    ${tx.amountUsd.toFixed(2)}
                  </Text>
                </Table.Td>
                <Table.Td>
                  <Badge 
                    variant="light" 
                    color="cyber"
                    style={{ fontFamily: 'JetBrains Mono' }}
                  >
                    +${tx.spareUsd.toFixed(2)}
                  </Badge>
                </Table.Td>
                <Table.Td>
                  <Badge 
                    variant={tx.status === 'NEW' ? 'dot' : 'filled'}
                    color={tx.status === 'NEW' ? 'neon' : 'green'}
                    size="lg"
                  >
                    {tx.status}
                  </Badge>
                </Table.Td>
                <Table.Td>
                  <Text size="sm" c="dimmed" style={{ fontFamily: 'JetBrains Mono' }}>
                    {new Date(tx.ts).toLocaleString()}
                  </Text>
                </Table.Td>
              </Table.Tr>
            ))}
            {displayedTransactions.length === 0 && (
              <Table.Tr>
                <Table.Td colSpan={6} style={{ textAlign: 'center' }}>
                  <Text c="dimmed">No transactions yet</Text>
                </Table.Td>
              </Table.Tr>
            )}
          </Table.Tbody>
        </Table>
      </Paper>
      
      <Modal
        opened={manualTxModalOpen}
        onClose={() => setManualTxModalOpen(false)}
        title={
          <Text size="lg" weight={600} transform="uppercase" style={{ letterSpacing: '1px' }}>
            Manual Transaction Entry
          </Text>
        }
        size="md"
        centered
        styles={{
          content: {
            backgroundColor: 'rgba(26, 27, 30, 0.95)',
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(14, 196, 255, 0.3)',
          },
          header: {
            backgroundColor: 'transparent',
            borderBottom: '1px solid rgba(14, 196, 255, 0.2)',
          },
        }}
      >
        <Stack gap="lg" mt="md">
          <TextInput
            label="Merchant"
            placeholder="e.g., Starbucks"
            value={manualTxData.merchant}
            onChange={(e) => setManualTxData({ ...manualTxData, merchant: e.target.value })}
            required
            styles={{
              label: {
                textTransform: 'uppercase',
                fontSize: '0.75rem',
                letterSpacing: '0.5px',
                color: '#0EC4FF',
              },
            }}
          />
          <NumberInput
            label="Amount (USD)"
            placeholder="e.g., 12.50"
            value={manualTxData.amountUsd}
            onChange={(value) => setManualTxData({ ...manualTxData, amountUsd: value })}
            min={0.01}
            precision={2}
            prefix="$"
            required
            styles={{
              label: {
                textTransform: 'uppercase',
                fontSize: '0.75rem',
                letterSpacing: '0.5px',
                color: '#0EC4FF',
              },
            }}
          />
          <Group justify="flex-end" mt="xl">
            <Button 
              variant="subtle" 
              onClick={() => setManualTxModalOpen(false)}
              color="gray"
            >
              CANCEL
            </Button>
            <Button
              onClick={() => {
                if (manualTxData.merchant && manualTxData.amountUsd) {
                  addManualTxMutation.mutate(manualTxData)
                }
              }}
              loading={addManualTxMutation.isPending}
              disabled={!manualTxData.merchant || !manualTxData.amountUsd}
              color="neon"
              variant="light"
              styles={{
                root: {
                  background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.2) 0%, rgba(106, 27, 255, 0.2) 100%)',
                  border: '1px solid rgba(14, 196, 255, 0.5)',
                }
              }}
            >
              <IconRocket size={16} style={{ marginRight: 8 }} />
              SUBMIT TRANSACTION
            </Button>
          </Group>
        </Stack>
      </Modal>
    </Stack>
    </>
  )
}

export default TransactionTab