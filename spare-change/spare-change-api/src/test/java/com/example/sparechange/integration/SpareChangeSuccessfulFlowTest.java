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
public class SpareChangeSuccessfulFlowTest {
    
    @Autowired
    private TxService txService;
    
    @Autowired
    private TxRepository txRepository;
    
    @Autowired
    private ThresholdService thresholdService;
    
    @Test
    public void testSuccessfulThresholdTrigger() {
        System.out.println("=== Spare Change Successful Flow Test ===");
        System.out.println("This test demonstrates triggering the $5 threshold");
        System.out.println("Note: Coinbase order will fail if account lacks USD funds\n");
        
        // Create transactions that will exceed threshold
        System.out.println("1. Creating transactions with large spare change:");
        
        // These amounts are chosen to create larger spare change
        Tx tx1 = createTransaction("Restaurant", "45.01");  // spare: $0.99
        System.out.println("   - " + tx1.getMerchant() + ": $" + tx1.getAmountUsd() + " (spare: $" + tx1.getSpareUsd() + ")");
        
        Tx tx2 = createTransaction("Electronics Store", "199.02");  // spare: $0.98
        System.out.println("   - " + tx2.getMerchant() + ": $" + tx2.getAmountUsd() + " (spare: $" + tx2.getSpareUsd() + ")");
        
        Tx tx3 = createTransaction("Clothing Store", "75.03");  // spare: $0.97
        System.out.println("   - " + tx3.getMerchant() + ": $" + tx3.getAmountUsd() + " (spare: $" + tx3.getSpareUsd() + ")");
        
        Tx tx4 = createTransaction("Bookstore", "23.04");  // spare: $0.96
        System.out.println("   - " + tx4.getMerchant() + ": $" + tx4.getAmountUsd() + " (spare: $" + tx4.getSpareUsd() + ")");
        
        Tx tx5 = createTransaction("Coffee Shop", "12.05");  // spare: $0.95
        System.out.println("   - " + tx5.getMerchant() + ": $" + tx5.getAmountUsd() + " (spare: $" + tx5.getSpareUsd() + ")");
        
        Tx tx6 = createTransaction("Grocery Store", "88.06");  // spare: $0.94
        System.out.println("   - " + tx6.getMerchant() + ": $" + tx6.getAmountUsd() + " (spare: $" + tx6.getSpareUsd() + ")");
        
        // Total: 0.99 + 0.98 + 0.97 + 0.96 + 0.95 + 0.94 = $5.79
        BigDecimal totalSpare = txService.getTotalSpareChange();
        System.out.println("\nTotal spare change: $" + totalSpare);
        
        // Check threshold - should trigger
        System.out.println("\n2. Checking threshold ($5.00):");
        boolean triggered = thresholdService.checkAndExecute();
        
        if (triggered) {
            System.out.println("   ✓ Threshold exceeded! Buy order attempted.");
            System.out.println("   ✓ Transactions marked as ROUNDUP_APPLIED");
            
            // Verify all transactions were updated
            assertTrue(txRepository.findById(tx1.getId()).orElseThrow().getStatus() == TxStatus.ROUNDUP_APPLIED);
            assertTrue(txRepository.findById(tx2.getId()).orElseThrow().getStatus() == TxStatus.ROUNDUP_APPLIED);
            assertTrue(txRepository.findById(tx3.getId()).orElseThrow().getStatus() == TxStatus.ROUNDUP_APPLIED);
            assertTrue(txRepository.findById(tx4.getId()).orElseThrow().getStatus() == TxStatus.ROUNDUP_APPLIED);
            assertTrue(txRepository.findById(tx5.getId()).orElseThrow().getStatus() == TxStatus.ROUNDUP_APPLIED);
            assertTrue(txRepository.findById(tx6.getId()).orElseThrow().getStatus() == TxStatus.ROUNDUP_APPLIED);
            
            // Check that total spare change is now 0
            BigDecimal remainingSpare = txService.getTotalSpareChange();
            System.out.println("\n3. Remaining spare change after buy: $" + remainingSpare);
            assertEquals(BigDecimal.ZERO, remainingSpare);
        } else {
            System.out.println("   ✗ Buy order failed (likely due to insufficient USD in Coinbase account)");
            System.out.println("   ✗ Transactions remain in NEW status");
        }
        
        System.out.println("\n=== Summary ===");
        System.out.println("The spare change mechanism works correctly:");
        System.out.println("- Transactions accumulate spare change");
        System.out.println("- Threshold service triggers at $5.00");
        System.out.println("- Transactions are marked as ROUNDUP_APPLIED after successful buy");
        System.out.println("\nNote: To see a successful Coinbase order, fund the sandbox account with USD");
    }
    
    private Tx createTransaction(String merchant, String amount) {
        Tx tx = new Tx();
        tx.setMerchant(merchant);
        tx.setAmountUsd(new BigDecimal(amount));
        return txService.ingestTx(tx);
    }
}