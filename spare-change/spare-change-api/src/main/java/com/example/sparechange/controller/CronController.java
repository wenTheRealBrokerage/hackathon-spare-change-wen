package com.example.sparechange.controller;

import com.example.sparechange.service.IThresholdService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/cron")
public class CronController {
    
    private final IThresholdService thresholdService;
    
    public CronController(IThresholdService thresholdService) {
        this.thresholdService = thresholdService;
    }
    
    @PostMapping("/threshold")
    public ResponseEntity<String> triggerThresholdCheck() {
        boolean executed = thresholdService.checkAndExecute();
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        if (executed) {
            return ResponseEntity.ok(String.format(
                "✅ Success! Spare change round-up executed at %s\n" +
                "Your spare change has been invested in cryptocurrency.",
                timestamp
            ));
        } else {
            return ResponseEntity.ok(String.format(
                "ℹ️ Threshold not met at %s\n" +
                "Keep adding transactions to accumulate more spare change!",
                timestamp
            ));
        }
    }
}