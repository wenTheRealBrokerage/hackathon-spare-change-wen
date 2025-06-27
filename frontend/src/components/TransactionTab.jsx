import { useState } from 'react'
import { Button, Stack, Group, Paper, Text, Badge, Table, ActionIcon, Title, NumberInput, Card, TextInput, Modal } from '@mantine/core'
import { notifications } from '@mantine/notifications'
import { useMutation, useQueryClient, useQuery } from '@tanstack/react-query'
import { IconPlus, IconMinus, IconEdit, IconShoppingCart, IconNetwork } from '@tabler/icons-react'
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
    <Stack gap="md">
      <Group justify="space-between">
        <Title order={3}>Live Transactions</Title>
        <Group>
          <Badge size="lg" variant="filled" color="teal">
            Total Spare: ${totalSpareChange.toFixed(2)}
          </Badge>
          <Badge color={isConnected ? 'green' : 'red'}>
            {isConnected ? 'Connected' : 'Disconnected'}
          </Badge>
        </Group>
      </Group>

      <Group justify="space-between" align="flex-start">
        <Group>
          <Button 
            onClick={() => addTxMutation.mutate()}
            loading={addTxMutation.isPending}
          >
            Add Random TX
          </Button>
          <Button 
            leftSection={<IconShoppingCart size={16} />}
            variant="outline"
            onClick={() => setManualTxModalOpen(true)}
          >
            Manual TX
          </Button>
          <Button 
            variant="light" 
            onClick={() => thresholdMutation.mutate()}
            loading={thresholdMutation.isPending}
          >
            Check spare change
          </Button>
          <Button 
            leftSection={<IconNetwork size={16} />}
            variant="subtle"
            onClick={() => diagnosticMutation.mutate()}
            loading={diagnosticMutation.isPending}
          >
            Diagnostic
          </Button>
        </Group>
        
        <Card shadow="sm" padding="sm" radius="md" withBorder>
          <Group>
            <div>
              <Text size="xs" c="dimmed">Buy Threshold</Text>
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
                    style={{ width: 100 }}
                  />
                  <ActionIcon 
                    size="sm" 
                    color="green" 
                    onClick={() => updateThresholdMutation.mutate(newThreshold)}
                    loading={updateThresholdMutation.isPending}
                  >
                    ✓
                  </ActionIcon>
                  <ActionIcon 
                    size="sm" 
                    color="red" 
                    onClick={() => {
                      setEditingThreshold(false)
                      setNewThreshold(thresholdData?.currentThreshold || 5)
                    }}
                  >
                    ✕
                  </ActionIcon>
                </Group>
              ) : (
                <Group gap="xs">
                  <Text fw={600}>${thresholdData?.currentThreshold || '5.00'}</Text>
                  <ActionIcon 
                    size="sm" 
                    variant="subtle" 
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
          </Group>
        </Card>
      </Group>

      <Paper shadow="xs" p="md">
        <Group justify="space-between" mb="sm">
          <Text size="sm" c="dimmed">
            Showing {displayedTransactions.length} of {transactions.length} transactions
          </Text>
          <Group gap="xs">
            <ActionIcon 
              variant="default" 
              onClick={() => setPageSize(Math.max(1, pageSize - 1))}
              disabled={pageSize <= 1}
            >
              <IconMinus size={16} />
            </ActionIcon>
            <Text size="sm">{pageSize} rows</Text>
            <ActionIcon 
              variant="default" 
              onClick={() => setPageSize(pageSize + 1)}
            >
              <IconPlus size={16} />
            </ActionIcon>
          </Group>
        </Group>

        <Table striped highlightOnHover>
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
              <Table.Tr key={tx.id}>
                <Table.Td>{tx.id}</Table.Td>
                <Table.Td>{tx.merchant}</Table.Td>
                <Table.Td>${tx.amountUsd.toFixed(2)}</Table.Td>
                <Table.Td>${tx.spareUsd.toFixed(2)}</Table.Td>
                <Table.Td>
                  <Badge color={tx.status === 'NEW' ? 'blue' : 'green'}>
                    {tx.status}
                  </Badge>
                </Table.Td>
                <Table.Td>{new Date(tx.ts).toLocaleString()}</Table.Td>
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
        title="Add Transaction"
      >
        <Stack>
          <TextInput
            label="Merchant"
            placeholder="e.g., Starbucks"
            value={manualTxData.merchant}
            onChange={(e) => setManualTxData({ ...manualTxData, merchant: e.target.value })}
            required
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
          />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setManualTxModalOpen(false)}>
              Cancel
            </Button>
            <Button
              onClick={() => {
                if (manualTxData.merchant && manualTxData.amountUsd) {
                  addManualTxMutation.mutate(manualTxData)
                }
              }}
              loading={addManualTxMutation.isPending}
              disabled={!manualTxData.merchant || !manualTxData.amountUsd}
            >
              Add Transaction
            </Button>
          </Group>
        </Stack>
      </Modal>
    </Stack>
  )
}

export default TransactionTab