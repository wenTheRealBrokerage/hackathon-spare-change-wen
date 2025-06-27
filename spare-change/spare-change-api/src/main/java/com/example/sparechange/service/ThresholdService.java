package com.example.sparechange.service;

import com.example.sparechange.client.ICoinbaseClient;
import com.example.sparechange.controller.ConfigController;
import com.example.sparechange.entity.RoundUpSummary;
import com.example.sparechange.entity.Tx;
import com.example.sparechange.entity.TxStatus;
import com.example.sparechange.repository.RoundUpSummaryRepository;
import com.example.sparechange.repository.TxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ThresholdService implements IThresholdService {
    
    private static final Logger log = LoggerFactory.getLogger(ThresholdService.class);
    
    private final TxRepository txRepository;
    private final RoundUpSummaryRepository roundUpSummaryRepository;
    private final ICoinbaseClient coinbaseClient;
    
    @Value("${coinbase.api.buy-threshold:5.00}")
    private BigDecimal buyThreshold;
    
    @Value("${coinbase.api.product-id:BTC-USD}")
    private String productId;
    
    @Autowired
    @Lazy
    private ConfigController configController;
    
    public ThresholdService(TxRepository txRepository,
                           RoundUpSummaryRepository roundUpSummaryRepository,
                           ICoinbaseClient coinbaseClient) {
        this.txRepository = txRepository;
        this.roundUpSummaryRepository = roundUpSummaryRepository;
        this.coinbaseClient = coinbaseClient;
    }
    
    @Scheduled(fixedDelayString = "${stacker.delay.mas:300000}")
    public void scheduledCheck() {
        log.info("Running scheduled threshold check");
        checkAndExecute();
    }
    
    @Transactional
    public boolean checkAndExecute() {
        log.info("Checking spare change threshold");
        
        // Step 1: Sum spareUsd of NEW transactions
        BigDecimal totalSpareChange = txRepository.sumSpareUsdByStatus(TxStatus.NEW);
        
        if (totalSpareChange == null) {
            totalSpareChange = BigDecimal.ZERO;
        }
        
        log.info("Total spare change available: ${}", totalSpareChange);
        
        // Get active threshold (either runtime updated or configured)
        BigDecimal activeThreshold = configController != null ? 
            configController.getActiveThreshold() : buyThreshold;
        
        // Step 2: Check if total >= threshold
        if (totalSpareChange.compareTo(activeThreshold) < 0) {
            log.info("Spare change ${} is below threshold ${}", totalSpareChange, activeThreshold);
            return false;
        }
        
        log.info("Threshold met! Executing Coinbase buy for ${}", totalSpareChange);
        
        try {
            // Get active product ID (either runtime updated or configured)
            String activeProductId = configController != null && configController.getActiveProductId() != null ? 
                configController.getActiveProductId() : productId;
            
            // Call Coinbase to buy crypto with USD
            String orderId = coinbaseClient.buyUsdToCrypto(totalSpareChange, activeProductId);
            log.info("Coinbase order created with ID: {}", orderId);
            
            // Create round-up summary record
            RoundUpSummary summary = new RoundUpSummary();
            summary.setTotalUsd(totalSpareChange);
            summary.setCreatedAt(LocalDateTime.now());
            summary.setCoinbaseOrderId(orderId);
            summary.setProductId(activeProductId);
            roundUpSummaryRepository.save(summary);
            
            // Update all NEW transactions to ROUNDUP_APPLIED
            List<Tx> newTransactions = txRepository.findByStatus(TxStatus.NEW, null).getContent();
            for (Tx tx : newTransactions) {
                tx.setStatus(TxStatus.ROUNDUP_APPLIED);
                tx.setCoinbaseOrderId(orderId);
            }
            txRepository.saveAll(newTransactions);
            
            log.info("Updated {} transactions to ROUNDUP_APPLIED status", newTransactions.size());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to execute Coinbase buy", e);
            throw new RuntimeException("Failed to execute round-up: " + e.getMessage(), e);
        }
    }
    
    public Page<RoundUpSummary> getRoundUpSummaries(Pageable pageable) {
        return roundUpSummaryRepository.findAll(pageable);
    }
    
    public List<RoundUpSummary> getAllRoundUpSummaries() {
        return roundUpSummaryRepository.findAll();
    }
}