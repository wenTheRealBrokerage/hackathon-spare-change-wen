package com.example.sparechange.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
public class CoinbaseApiDebugTest {
    
    @Autowired
    private CoinbaseClient coinbaseClient;
    
    @Test
    public void debugEthOrderIssue() {
        System.out.println("\n=== Testing ETH-USD Order Creation ===");
        
        // Test with different amounts
        BigDecimal[] amounts = {new BigDecimal("10.00"), new BigDecimal("25.00"), new BigDecimal("50.00")};
        
        for (BigDecimal amount : amounts) {
            System.out.println("\nTrying to buy ETH-USD with $" + amount);
            String orderId = coinbaseClient.buyUsdToCrypto(amount, "ETH-USD");
            System.out.println("Result: " + orderId);
            
            // Also test BTC for comparison
            System.out.println("\nTrying to buy BTC-USD with $" + amount);
            String btcOrderId = coinbaseClient.buyUsdToCrypto(amount, "BTC-USD");
            System.out.println("Result: " + btcOrderId);
        }
    }
}