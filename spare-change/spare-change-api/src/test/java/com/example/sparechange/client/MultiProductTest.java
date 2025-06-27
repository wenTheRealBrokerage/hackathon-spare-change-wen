package com.example.sparechange.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MultiProductTest {
    
    @Autowired
    private CoinbaseClient coinbaseClient;
    
    @Test
    public void testBuyBitcoin() {
        System.out.println("Testing Bitcoin purchase with spare change...");
        
        BigDecimal amount = new BigDecimal("5.00");
        String orderId = coinbaseClient.buyUsdToCrypto(amount, "BTC-USD");
        
        assertNotNull(orderId);
        assertTrue(orderId != null && !orderId.isEmpty(), "Order ID should not be empty");
        System.out.println("Created BTC order: " + orderId);
    }
    
    @Test
    public void testBuyEthereum() {
        System.out.println("Testing Ethereum purchase with spare change...");
        
        BigDecimal amount = new BigDecimal("5.00");
        String orderId = coinbaseClient.buyUsdToCrypto(amount, "ETH-USD");
        
        assertNotNull(orderId);
        assertTrue(orderId != null && !orderId.isEmpty(), "Order ID should not be empty");
        System.out.println("Created ETH order: " + orderId);
    }
    
    @Test
    public void testListMultiProductOrders() {
        System.out.println("Testing list orders for multiple products...");
        
        // Create some test orders
        String btcOrder1 = coinbaseClient.buyUsdToCrypto(new BigDecimal("10.00"), "BTC-USD");
        String ethOrder = coinbaseClient.buyUsdToCrypto(new BigDecimal("15.00"), "ETH-USD");
        String btcOrder2 = coinbaseClient.buyUsdToCrypto(new BigDecimal("20.00"), "BTC-USD");
        
        // Verify orders were created
        assertNotNull(btcOrder1);
        assertNotNull(ethOrder);
        assertNotNull(btcOrder2);
        
        System.out.println("Created orders: BTC1=" + btcOrder1 + ", ETH=" + ethOrder + ", BTC2=" + btcOrder2);
        
        // List all orders
        var orders = coinbaseClient.listOrders();
        
        assertNotNull(orders);
        assertTrue(orders.size() >= 3, 
            "Expected at least 3 orders but found " + orders.size());
        
        // Check we have both BTC and ETH orders
        // Filter by the order IDs we just created
        boolean hasBtcOrder = orders.stream().anyMatch(o -> 
            o.getId().equals(btcOrder1) || o.getId().equals(btcOrder2));
        boolean hasEthOrder = orders.stream().anyMatch(o -> 
            o.getId().equals(ethOrder));
        
        assertTrue(hasBtcOrder, "Should have BTC orders");
        assertTrue(hasEthOrder, "Should have ETH order");
        
        System.out.println("Found " + orders.size() + " orders with multiple products");
    }
}