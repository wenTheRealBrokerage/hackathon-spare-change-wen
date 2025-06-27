package com.example.sparechange.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/config")
public class ConfigController {
    
    @Value("${coinbase.api.buy-threshold:5.00}")
    private BigDecimal currentThreshold;
    
    @Value("${stacker.delay.mas:300000}")
    private Long schedulerDelay;
    
    private final Environment environment;
    BigDecimal runtimeThreshold; // package-private for testing
    
    public ConfigController(Environment environment) {
        this.environment = environment;
    }
    
    @GetMapping("/threshold")
    public ResponseEntity<Map<String, Object>> getThreshold() {
        Map<String, Object> response = new HashMap<>();
        
        // Use runtime threshold if it's been updated, otherwise use the configured value
        BigDecimal activeThreshold = runtimeThreshold != null ? runtimeThreshold : currentThreshold;
        
        response.put("currentThreshold", activeThreshold);
        response.put("defaultThreshold", new BigDecimal("5.00"));
        response.put("currency", "USD");
        response.put("description", "Minimum spare change amount required to trigger Bitcoin purchase");
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/threshold")
    public ResponseEntity<Map<String, Object>> updateThreshold(@RequestBody Map<String, String> request) {
        try {
            String newThresholdStr = request.get("threshold");
            if (newThresholdStr == null || newThresholdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Threshold value is required",
                    "example", Map.of("threshold", "10.00")
                ));
            }
            
            BigDecimal newThreshold = new BigDecimal(newThresholdStr);
            
            // Validate threshold
            if (newThreshold.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Threshold must be greater than 0"
                ));
            }
            
            if (newThreshold.compareTo(new BigDecimal("1000")) > 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Threshold cannot exceed $1000"
                ));
            }
            
            // Update runtime threshold
            this.runtimeThreshold = newThreshold;
            
            Map<String, Object> response = new HashMap<>();
            response.put("previousThreshold", currentThreshold);
            response.put("newThreshold", newThreshold);
            response.put("status", "updated");
            response.put("note", "This change is temporary and will reset on application restart. Update application.yaml for permanent change.");
            
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid threshold format. Please provide a valid decimal number.",
                "example", Map.of("threshold", "10.00")
            ));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // Threshold configuration
        BigDecimal activeThreshold = runtimeThreshold != null ? runtimeThreshold : currentThreshold;
        config.put("threshold", Map.of(
            "current", activeThreshold,
            "default", new BigDecimal("5.00"),
            "min", new BigDecimal("0.01"),
            "max", new BigDecimal("1000.00"),
            "currency", "USD"
        ));
        
        // Scheduler configuration
        config.put("scheduler", Map.of(
            "delayMs", schedulerDelay,
            "delayMinutes", schedulerDelay / 60000,
            "description", "Frequency of automatic threshold checks"
        ));
        
        // Coinbase configuration (without sensitive data)
        config.put("coinbase", Map.of(
            "environment", "sandbox",
            "baseUrl", environment.getProperty("coinbase.api.base-url", "https://api-public.sandbox.exchange.coinbase.com")
        ));
        
        return ResponseEntity.ok(config);
    }
    
    public BigDecimal getActiveThreshold() {
        return runtimeThreshold != null ? runtimeThreshold : currentThreshold;
    }
}