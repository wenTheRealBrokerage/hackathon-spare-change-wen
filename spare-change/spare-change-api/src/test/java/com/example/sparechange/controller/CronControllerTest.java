package com.example.sparechange.controller;

import com.example.sparechange.service.IThresholdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CronController.class)
class CronControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IThresholdService thresholdService;
    
    @Test
    void triggerThresholdCheck_WhenExecuted_ShouldReturnSuccess() throws Exception {
        // Given
        when(thresholdService.checkAndExecute()).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/cron/threshold"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("✅ Success! Spare change round-up executed")));
    }
    
    @Test
    void triggerThresholdCheck_WhenNotExecuted_ShouldReturnThresholdNotMet() throws Exception {
        // Given
        when(thresholdService.checkAndExecute()).thenReturn(false);
        
        // When & Then
        mockMvc.perform(post("/cron/threshold"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ℹ️ Threshold not met")));
    }
}