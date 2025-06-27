package com.example.sparechange.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ConfigControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ConfigController configController;
    
    @BeforeEach
    void setUp() {
        // Reset runtime threshold before each test
        configController.runtimeThreshold = null;
    }
    
    @Test
    public void testGetThreshold() throws Exception {
        mockMvc.perform(get("/config/threshold"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentThreshold", is(5.0)))
                .andExpect(jsonPath("$.defaultThreshold", is(5.0)))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.description", notNullValue()));
    }
    
    @Test
    public void testUpdateThreshold() throws Exception {
        // Update threshold to $10
        String requestBody = """
            {
                "threshold": "10.00"
            }
            """;
        
        mockMvc.perform(put("/config/threshold")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previousThreshold", is(5.0)))
                .andExpect(jsonPath("$.newThreshold", is(10.0)))
                .andExpect(jsonPath("$.status", is("updated")))
                .andExpect(jsonPath("$.note", containsString("temporary")));
        
        // Verify the threshold was updated
        mockMvc.perform(get("/config/threshold"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentThreshold", is(10.0)));
    }
    
    @Test
    public void testUpdateThresholdValidation() throws Exception {
        // Test invalid threshold (negative)
        mockMvc.perform(put("/config/threshold")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"threshold\": \"-5.00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("greater than 0")));
        
        // Test invalid threshold (too high)
        mockMvc.perform(put("/config/threshold")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"threshold\": \"1500.00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("cannot exceed $1000")));
        
        // Test invalid format
        mockMvc.perform(put("/config/threshold")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"threshold\": \"invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Invalid threshold format")));
        
        // Test missing threshold
        mockMvc.perform(put("/config/threshold")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("required")));
    }
    
    @Test
    public void testGetAllConfig() throws Exception {
        mockMvc.perform(get("/config/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.threshold.current", is(5.0)))
                .andExpect(jsonPath("$.threshold.min", is(0.01)))
                .andExpect(jsonPath("$.threshold.max", is(1000.0)))
                .andExpect(jsonPath("$.scheduler.delayMs", is(300000)))
                .andExpect(jsonPath("$.scheduler.delayMinutes", is(5)))
                .andExpect(jsonPath("$.coinbase.environment", is("sandbox")))
                .andExpect(jsonPath("$.coinbase.baseUrl", notNullValue()));
    }
    
    @Test
    public void testThresholdPersistenceAcrossRequests() throws Exception {
        // Update to $7.50
        mockMvc.perform(put("/config/threshold")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"threshold\": \"7.50\"}"))
                .andExpect(status().isOk());
        
        // Update to $15.00
        mockMvc.perform(put("/config/threshold")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"threshold\": \"15.00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previousThreshold", is(5.0))) // Still shows original
                .andExpect(jsonPath("$.newThreshold", is(15.0)));
        
        // Verify current is 15.00
        mockMvc.perform(get("/config/threshold"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentThreshold", is(15.0)));
    }
}