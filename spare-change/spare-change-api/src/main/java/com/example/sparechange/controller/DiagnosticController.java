package com.example.sparechange.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DiagnosticController {
    
    private final WebClient webClient;
    
    public DiagnosticController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.ipify.org").build();
    }
    
    @GetMapping("/api/diagnostic/ip")
    public Mono<Map<String, String>> getOutboundIp() {
        return webClient.get()
                .uri("?format=text")
                .retrieve()
                .bodyToMono(String.class)
                .map(ip -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("outboundIp", ip.trim());
                    response.put("message", "Add this IP to your Coinbase whitelist");
                    return response;
                });
    }
}