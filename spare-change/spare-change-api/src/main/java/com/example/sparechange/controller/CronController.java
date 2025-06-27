package com.example.sparechange.controller;

import com.example.sparechange.service.IThresholdService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cron")
public class CronController {
    
    private final IThresholdService thresholdService;
    
    public CronController(IThresholdService thresholdService) {
        this.thresholdService = thresholdService;
    }
    
    @PostMapping("/threshold")
    public ThresholdCheckResponse triggerThresholdCheck() {
        boolean executed = thresholdService.checkAndExecute();
        
        ThresholdCheckResponse response = new ThresholdCheckResponse();
        response.setExecuted(executed);
        response.setMessage(executed ? "Round-up executed successfully" : "Threshold not met");
        response.setTimestamp(System.currentTimeMillis());
        
        return response;
    }
    
    static class ThresholdCheckResponse {
        private boolean executed;
        private String message;
        private long timestamp;
        
        public boolean isExecuted() { return executed; }
        public void setExecuted(boolean executed) { this.executed = executed; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}