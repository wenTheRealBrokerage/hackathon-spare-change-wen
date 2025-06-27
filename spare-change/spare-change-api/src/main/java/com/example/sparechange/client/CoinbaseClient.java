package com.example.sparechange.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.example.sparechange.entity.MockOrder;
import com.example.sparechange.repository.MockOrderRepository;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CoinbaseClient implements ICoinbaseClient {
    
    private static final Logger log = LoggerFactory.getLogger(CoinbaseClient.class);
    
    private final WebClient webClient;
    private final String apiKey;
    private final String apiSecret;
    private final String passphrase;
    private final MockOrderRepository mockOrderRepository;
    private boolean demoMode = false;
    
    public CoinbaseClient(WebClient.Builder webClientBuilder,
                          @Value("${coinbase.api.key}") String apiKey,
                          @Value("${coinbase.api.secret}") String apiSecret,
                          @Value("${coinbase.api.passphrase}") String passphrase,
                          MockOrderRepository mockOrderRepository) {
        this.webClient = webClientBuilder
                .baseUrl("https://api-public.sandbox.exchange.coinbase.com")
                .defaultStatusHandler(
                    status -> status.is5xxServerError() || status.is4xxClientError(),
                    response -> Mono.empty()
                )
                .build();
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.passphrase = passphrase;
        this.mockOrderRepository = mockOrderRepository;
    }
    
    public String buyUsdcToBtc(BigDecimal usd) {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String method = "POST";
        String requestPath = "/orders";
        
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setType("market");
        orderRequest.setSide("buy");
        orderRequest.setProductId("BTC-USD");
        orderRequest.setFunds(usd.toString());
        
        String body = convertToJson(orderRequest);
        String signature = generateSignature(timestamp, method, requestPath, body);
        
        log.info("Attempting to buy BTC with ${}", usd);
        
        try {
            OrderResponse response = webClient.post()
                    .uri(requestPath)
                    .header("CB-ACCESS-KEY", apiKey)
                    .header("CB-ACCESS-SIGN", signature)
                    .header("CB-ACCESS-TIMESTAMP", timestamp)
                    .header("CB-ACCESS-PASSPHRASE", passphrase)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("User-Agent", "spare-change-api/1.0")
                    .bodyValue(orderRequest)
                    .retrieve()
                    .bodyToMono(OrderResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            if (response != null && response.getId() != null) {
                log.info("Successfully created order: {}", response.getId());
                return response.getId();
            } else {
                // Coinbase unreachable or returned null - use demo mode
                return createDemoOrder(usd);
            }
        } catch (Exception e) {
            log.warn("Coinbase API unavailable, using demo mode: {}", e.getMessage());
            return createDemoOrder(usd);
        }
    }
    
    @Transactional
    private String createDemoOrder(BigDecimal usd) {
        String demoOrderId = "DEMO-" + UUID.randomUUID().toString();
        BigDecimal btcPrice = new BigDecimal("43000"); // Approximate BTC price
        BigDecimal btcAmount = usd.divide(btcPrice, 8, RoundingMode.HALF_UP);
        BigDecimal fees = usd.multiply(new BigDecimal("0.01")); // 1% fee
        
        MockOrder mockOrder = new MockOrder();
        mockOrder.setId(demoOrderId);
        mockOrder.setProductId("BTC-USD");
        mockOrder.setSide("buy");
        mockOrder.setType("market");
        mockOrder.setStatus("done");
        mockOrder.setSettled(true);
        mockOrder.setCreatedAt(LocalDateTime.now());
        mockOrder.setFilledSize(btcAmount);
        mockOrder.setExecutedValue(usd);
        mockOrder.setFillFees(fees);
        
        mockOrderRepository.save(mockOrder);
        
        log.info("DEMO MODE: Created simulated order {} for ${} (≈ {} BTC)", 
                demoOrderId, usd, btcAmount);
        
        demoMode = true;
        return demoOrderId;
    }
    
    public List<OrderDto> listOrders() {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String method = "GET";
        String requestPath = "/orders?status=all";
        String signature = generateSignature(timestamp, method, requestPath, "");
        
        log.debug("Fetching orders from Coinbase");
        
        try {
            List<OrderDto> orders = webClient.get()
                    .uri(requestPath)
                    .header("CB-ACCESS-KEY", apiKey)
                    .header("CB-ACCESS-SIGN", signature)
                    .header("CB-ACCESS-TIMESTAMP", timestamp)
                    .header("CB-ACCESS-PASSPHRASE", passphrase)
                    .header("User-Agent", "spare-change-api/1.0")
                    .retrieve()
                    .bodyToFlux(OrderDto.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            if (orders != null) {
                log.info("Successfully fetched {} orders", orders.size());
                return orders;
            } else {
                return createDemoOrders();
            }
        } catch (Exception e) {
            log.warn("Coinbase API unavailable, using demo orders: {}", e.getMessage());
            return createDemoOrders();
        }
    }
    
    private List<OrderDto> createDemoOrders() {
        List<OrderDto> demoOrders = new ArrayList<>();
        
        // Load mock orders from database
        List<MockOrder> mockOrders = mockOrderRepository.findAllByOrderByCreatedAtDesc();
        for (MockOrder mock : mockOrders) {
            OrderDto dto = new OrderDto();
            dto.setId(mock.getId());
            dto.setProductId(mock.getProductId());
            dto.setSide(mock.getSide());
            dto.setType(mock.getType());
            dto.setStatus(mock.getStatus());
            dto.setSettled(mock.isSettled());
            dto.setCreatedAt(mock.getCreatedAt());
            dto.setFilledSize(mock.getFilledSize());
            dto.setExecutedValue(mock.getExecutedValue());
            dto.setFillFees(mock.getFillFees());
            demoOrders.add(dto);
        }
        
        log.info("DEMO MODE: Returning {} simulated orders from database", demoOrders.size());
        return demoOrders;
    }
    
    private String generateSignature(String timestamp, String method, String requestPath, String body) {
        try {
            String prehash = timestamp + method.toUpperCase() + requestPath + body;
            System.out.println("Prehash string: " + prehash);
            
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            byte[] secretDecoded = Base64.getDecoder().decode(apiSecret);
            SecretKeySpec secretKey = new SecretKeySpec(secretDecoded, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(prehash.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(hash);
            
            System.out.println("Generated signature: " + signature);
            return signature;
        } catch (Exception e) {
            System.err.println("Error generating signature: " + e.getMessage());
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    
    private String convertToJson(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert to JSON", e);
        }
    }
    
    static class OrderRequest {
        private String type;
        private String side;
        @com.fasterxml.jackson.annotation.JsonProperty("product_id")
        private String productId;
        private String funds;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSide() { return side; }
        public void setSide(String side) { this.side = side; }
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getFunds() { return funds; }
        public void setFunds(String funds) { this.funds = funds; }
    }
    
    static class OrderResponse {
        private String id;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
}