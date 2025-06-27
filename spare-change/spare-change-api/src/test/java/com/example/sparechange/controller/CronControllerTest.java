package com.example.sparechange.controller;

import com.example.sparechange.service.ThresholdService;
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
    private ThresholdService thresholdService;
    
    @Test
    void triggerThresholdCheck_WhenExecuted_ShouldReturnSuccess() throws Exception {
        // Given
        when(thresholdService.checkAndExecute()).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/cron/threshold"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executed").value(true))
                .andExpect(jsonPath("$.message").value("Round-up executed successfully"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void triggerThresholdCheck_WhenNotExecuted_ShouldReturnThresholdNotMet() throws Exception {
        // Given
        when(thresholdService.checkAndExecute()).thenReturn(false);
        
        // When & Then
        mockMvc.perform(post("/cron/threshold"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executed").value(false))
                .andExpect(jsonPath("$.message").value("Threshold not met"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}