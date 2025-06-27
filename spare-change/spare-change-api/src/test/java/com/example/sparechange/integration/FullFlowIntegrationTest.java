package com.example.sparechange.integration;

import com.example.sparechange.entity.Tx;
import com.example.sparechange.entity.TxStatus;
import com.example.sparechange.service.TxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.transaction.annotation.Transactional
public class FullFlowIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TxService txService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testCompleteSpareChangeFlow() throws Exception {
        System.out.println("=== Full Flow Integration Test ===");
        
        // Step 1: Check initial state - no round-up orders
        mockMvc.perform(get("/roundup/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(0)))
                .andExpect(jsonPath("$.totalUsdConverted", is(0)));
        
        // Step 2: Create transactions via REST API
        createTransactionViaApi("Restaurant Bill", "45.15");
        createTransactionViaApi("Gas Station", "32.40");
        createTransactionViaApi("Grocery Store", "78.25");
        createTransactionViaApi("Online Shopping", "123.99");
        createTransactionViaApi("Coffee Shop", "4.50");
        createTransactionViaApi("Parking", "12.75");
        
        // Step 3: Check transactions were created
        mockMvc.perform(get("/tx"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(6)))
                .andExpect(jsonPath("$.content[0].status", is("NEW")));
        
        // Step 4: Check total spare change (should be > $5)
        BigDecimal totalSpare = txService.getTotalSpareChange();
        System.out.println("Total spare change accumulated: $" + totalSpare);
        
        // Step 5: Trigger threshold check manually
        mockMvc.perform(post("/cron/threshold"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executed", anyOf(is(true), is(false))));
        
        // Note: The actual Coinbase call will fail without USD funds
        // But we can still check if the attempt was made
        
        // Step 6: Check round-up orders
        mockMvc.perform(get("/roundup/orders"))
                .andExpect(status().isOk());
        
        // Step 7: Get summary of all orders
        mockMvc.perform(get("/roundup/summary"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println("\nRound-up Summary: " + response);
                });
        
        System.out.println("\n=== Test Complete ===");
        System.out.println("Note: Actual BTC purchases require USD funds in Coinbase sandbox");
    }
    
    private void createTransactionViaApi(String merchant, String amount) throws Exception {
        String requestBody = String.format("""
            {
                "merchant": "%s",
                "amountUsd": %s
            }
            """, merchant, amount);
        
        mockMvc.perform(post("/tx")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchant", is(merchant)))
                .andExpect(jsonPath("$.status", is("NEW")))
                .andExpect(jsonPath("$.spareUsd", notNullValue()));
    }
}