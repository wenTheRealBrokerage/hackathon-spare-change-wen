import { useEffect, useState, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../utils/api'

export const useTransactionStream = () => {
  const [streamTransactions, setStreamTransactions] = useState([])
  const [isConnected, setIsConnected] = useState(false)

  // Poll for transaction updates every 2 seconds to catch status changes
  const { data: polledData } = useQuery({
    queryKey: ['transactions'],
    queryFn: api.getTransactions,
    refetchInterval: 2000,
  })

  useEffect(() => {
    const eventSource = new EventSource('/api/tx/stream')
    
    eventSource.onopen = () => {
      setIsConnected(true)
    }
    
    eventSource.onmessage = (event) => {
      try {
        const transaction = JSON.parse(event.data)
        setStreamTransactions(prev => {
          // Check if transaction already exists
          const exists = prev.some(tx => tx.id === transaction.id)
          if (exists) {
            // Update existing transaction
            return prev.map(tx => tx.id === transaction.id ? transaction : tx)
          } else {
            // Add new transaction at the beginning
            return [transaction, ...prev].slice(0, 100)
          }
        })
      } catch (error) {
        console.error('Failed to parse transaction:', error)
      }
    }
    
    eventSource.onerror = () => {
      setIsConnected(false)
    }
    
    return () => {
      eventSource.close()
    }
  }, [])

  // Use polled data as the primary source if available, otherwise use stream data
  const transactions = useMemo(() => {
    if (polledData?.content) {
      // Return the polled data which has the latest status updates
      return polledData.content
    }
    // If no polled data yet, return stream transactions
    return streamTransactions
  }, [polledData, streamTransactions])

  return { transactions, isConnected }
}