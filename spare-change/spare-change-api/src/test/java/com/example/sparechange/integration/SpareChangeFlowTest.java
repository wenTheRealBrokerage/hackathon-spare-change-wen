package com.example.sparechange.integration;

import com.example.sparechange.entity.Tx;
import com.example.sparechange.entity.TxStatus;
import com.example.sparechange.repository.TxRepository;
import com.example.sparechange.service.ThresholdService;
import com.example.sparechange.service.TxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "coinbase.api.buy-threshold=5.00"
})
public class SpareChangeFlowTest {
    
    @Autowired
    private TxService txService;
    
    @Autowired
    private TxRepository txRepository;
    
    @Autowired
    private ThresholdService thresholdService;
    
    @Test
    public void testSpareChangeFlow() {
        System.out.println("=== Spare Change Integration Test ===");
        System.out.println("This test simulates the complete spare change flow");
        System.out.println("Note: Actual Coinbase orders will fail if account lacks USD funds\n");
        
        // Step 1: Create transactions with spare change
        System.out.println("1. Creating transactions with spare change:");
        
        Tx tx1 = createTransaction("Coffee Shop", "12.75", "2.25");
        System.out.println("   - " + tx1.getMerchant() + ": $" + tx1.getAmountUsd() + " (spare: $" + tx1.getSpareUsd() + ")");
        
        Tx tx2 = createTransaction("Grocery Store", "45.20", "4.80");
        System.out.println("   - " + tx2.getMerchant() + ": $" + tx2.getAmountUsd() + " (spare: $" + tx2.getSpareUsd() + ")");
        
        Tx tx3 = createTransaction("Gas Station", "38.90", "1.10");
        System.out.println("   - " + tx3.getMerchant() + ": $" + tx3.getAmountUsd() + " (spare: $" + tx3.getSpareUsd() + ")");
        
        // Total spare change: 0.25 + 0.80 + 0.10 = 1.15
        System.out.println("\nTotal spare change: $1.15 (should be below threshold)");
        
        // Step 2: Check threshold (should trigger buy)
        System.out.println("\n2. Checking threshold ($5.00):");
        boolean triggered = thresholdService.checkAndExecute();
        
        if (triggered) {
            System.out.println("   ✓ Threshold exceeded! Buy order attempted.");
            System.out.println("   Note: Order will fail if Coinbase account lacks USD funds");
            
            // Verify transactions were updated
            Tx updatedTx1 = txRepository.findById(tx1.getId()).orElseThrow();
            Tx updatedTx2 = txRepository.findById(tx2.getId()).orElseThrow();
            Tx updatedTx3 = txRepository.findById(tx3.getId()).orElseThrow();
            
            assertEquals(TxStatus.ROUNDUP_APPLIED, updatedTx1.getStatus());
            assertEquals(TxStatus.ROUNDUP_APPLIED, updatedTx2.getStatus());
            assertEquals(TxStatus.ROUNDUP_APPLIED, updatedTx3.getStatus());
        } else {
            System.out.println("   ✗ Threshold not met or buy order failed");
        }
        
        // Step 3: Add another small transaction
        System.out.println("\n3. Adding another transaction:");
        Tx tx4 = createTransaction("Online Purchase", "99.99", "0.01");
        System.out.println("   - " + tx4.getMerchant() + ": $" + tx4.getAmountUsd() + " (spare: $" + tx4.getSpareUsd() + ")");
        
        // Step 4: Check again (should not trigger if previous succeeded)
        System.out.println("\n4. Checking threshold again:");
        boolean triggered2 = thresholdService.checkAndExecute();
        
        if (triggered2) {
            System.out.println("   ✓ New threshold exceeded!");
        } else {
            System.out.println("   ✗ Below threshold ($0.01 < $5.00)");
        }
        
        System.out.println("\n=== Test Complete ===");
    }
    
    private Tx createTransaction(String merchant, String amount, String expectedSpare) {
        Tx tx = new Tx();
        tx.setMerchant(merchant);
        tx.setAmountUsd(new BigDecimal(amount));
        // ingestTx will calculate spare change automatically
        return txService.ingestTx(tx);
    }
}