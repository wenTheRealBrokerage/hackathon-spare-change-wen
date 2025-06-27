package com.example.sparechange.service;

import com.example.sparechange.client.ICoinbaseClient;
import com.example.sparechange.entity.RoundUpSummary;
import com.example.sparechange.entity.Tx;
import com.example.sparechange.entity.TxStatus;
import com.example.sparechange.repository.RoundUpSummaryRepository;
import com.example.sparechange.repository.TxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThresholdServiceTest {
    
    @Mock
    private TxRepository txRepository;
    
    @Mock
    private RoundUpSummaryRepository roundUpSummaryRepository;
    
    @Mock
    private ICoinbaseClient coinbaseClient;
    
    private ThresholdService thresholdService;
    
    @BeforeEach
    void setUp() {
        thresholdService = new ThresholdService(txRepository, roundUpSummaryRepository, coinbaseClient);
        ReflectionTestUtils.setField(thresholdService, "buyThreshold", new BigDecimal("5.00"));
        ReflectionTestUtils.setField(thresholdService, "productId", "BTC-USD");
    }
    
    @Test
    void checkAndExecute_WhenBelowThreshold_ShouldReturnFalse() {
        // Given
        when(txRepository.sumSpareUsdByStatus(TxStatus.NEW)).thenReturn(new BigDecimal("3.50"));
        
        // When
        boolean result = thresholdService.checkAndExecute();
        
        // Then
        assertFalse(result);
        verify(coinbaseClient, never()).buyUsdToCrypto(any(), any());
        verify(roundUpSummaryRepository, never()).save(any());
    }
    
    @Test
    void checkAndExecute_WhenExactlyThreshold_ShouldExecuteBuy() {
        // Given
        BigDecimal totalSpare = new BigDecimal("5.00");
        String orderId = "ORDER-123";
        
        Tx tx1 = createTx(1L, "2.50");
        Tx tx2 = createTx(2L, "2.50");
        List<Tx> transactions = Arrays.asList(tx1, tx2);
        Page<Tx> page = new PageImpl<>(transactions);
        
        when(txRepository.sumSpareUsdByStatus(TxStatus.NEW)).thenReturn(totalSpare);
        when(txRepository.findByStatus(eq(TxStatus.NEW), any())).thenReturn(page);
        when(coinbaseClient.buyUsdToCrypto(totalSpare, "BTC-USD")).thenReturn(orderId);
        
        // When
        boolean result = thresholdService.checkAndExecute();
        
        // Then
        assertTrue(result);
        verify(coinbaseClient).buyUsdToCrypto(totalSpare, "BTC-USD");
        
        // Verify round-up summary saved
        ArgumentCaptor<RoundUpSummary> summaryCaptor = ArgumentCaptor.forClass(RoundUpSummary.class);
        verify(roundUpSummaryRepository).save(summaryCaptor.capture());
        RoundUpSummary savedSummary = summaryCaptor.getValue();
        assertEquals(totalSpare, savedSummary.getTotalUsd());
        assertEquals(orderId, savedSummary.getCoinbaseOrderId());
        assertNotNull(savedSummary.getCreatedAt());
        
        // Verify transactions updated
        ArgumentCaptor<List<Tx>> txCaptor = ArgumentCaptor.forClass(List.class);
        verify(txRepository).saveAll(txCaptor.capture());
        List<Tx> updatedTxs = txCaptor.getValue();
        assertEquals(2, updatedTxs.size());
        updatedTxs.forEach(tx -> {
            assertEquals(TxStatus.ROUNDUP_APPLIED, tx.getStatus());
            assertEquals(orderId, tx.getCoinbaseOrderId());
        });
    }
    
    @Test
    void checkAndExecute_WhenAboveThreshold_ShouldExecuteBuy() {
        // Given
        BigDecimal totalSpare = new BigDecimal("10.75");
        String orderId = "ORDER-456";
        
        Tx tx1 = createTx(1L, "3.25");
        Tx tx2 = createTx(2L, "4.50");
        Tx tx3 = createTx(3L, "3.00");
        List<Tx> transactions = Arrays.asList(tx1, tx2, tx3);
        Page<Tx> page = new PageImpl<>(transactions);
        
        when(txRepository.sumSpareUsdByStatus(TxStatus.NEW)).thenReturn(totalSpare);
        when(txRepository.findByStatus(eq(TxStatus.NEW), any())).thenReturn(page);
        when(coinbaseClient.buyUsdToCrypto(totalSpare, "BTC-USD")).thenReturn(orderId);
        
        // When
        boolean result = thresholdService.checkAndExecute();
        
        // Then
        assertTrue(result);
        verify(coinbaseClient).buyUsdToCrypto(totalSpare, "BTC-USD");
        verify(roundUpSummaryRepository).save(any(RoundUpSummary.class));
        verify(txRepository).saveAll(argThat(list -> ((List<Tx>)list).size() == 3));
    }
    
    @Test
    void checkAndExecute_WhenCoinbaseThrowsException_ShouldPropagateException() {
        // Given
        BigDecimal totalSpare = new BigDecimal("5.00");
        when(txRepository.sumSpareUsdByStatus(TxStatus.NEW)).thenReturn(totalSpare);
        when(coinbaseClient.buyUsdToCrypto(any(), any())).thenThrow(new RuntimeException("Coinbase API error"));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> thresholdService.checkAndExecute());
        
        assertTrue(exception.getMessage().contains("Failed to execute round-up"));
        verify(roundUpSummaryRepository, never()).save(any());
        verify(txRepository, never()).saveAll(any());
    }
    
    @Test
    void checkAndExecute_WhenNoNewTransactions_ShouldReturnFalse() {
        // Given
        when(txRepository.sumSpareUsdByStatus(TxStatus.NEW)).thenReturn(null);
        
        // When
        boolean result = thresholdService.checkAndExecute();
        
        // Then
        assertFalse(result);
        verify(coinbaseClient, never()).buyUsdToCrypto(any(), any());
    }
    
    private Tx createTx(Long id, String spareAmount) {
        Tx tx = new Tx();
        tx.setId(id);
        tx.setMerchant("Test Merchant");
        tx.setAmountUsd(new BigDecimal("10.00"));
        tx.setSpareUsd(new BigDecimal(spareAmount));
        tx.setStatus(TxStatus.NEW);
        tx.setTs(LocalDateTime.now());
        return tx;
    }
}