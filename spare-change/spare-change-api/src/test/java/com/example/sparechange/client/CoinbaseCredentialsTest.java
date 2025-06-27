package com.example.sparechange.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CoinbaseCredentialsTest {
    
    @Value("${coinbase.api.key}")
    private String apiKey;
    
    @Value("${coinbase.api.secret}")
    private String apiSecret;
    
    @Value("${coinbase.api.passphrase}")
    private String passphrase;
    
    @Test
    public void printCredentials() {
        System.out.println("=== Coinbase Credentials Check ===");
        System.out.println("API Key: " + apiKey);
        System.out.println("API Key Length: " + apiKey.length());
        System.out.println("Secret (first 10 chars): " + apiSecret.substring(0, Math.min(10, apiSecret.length())) + "...");
        System.out.println("Secret Length: " + apiSecret.length());
        System.out.println("Passphrase: '" + passphrase + "'");
        System.out.println("Passphrase Length: " + passphrase.length());
        
        // Check for common issues
        if (passphrase.contains(" ")) {
            System.out.println("WARNING: Passphrase contains spaces!");
        }
        if (passphrase.startsWith(" ") || passphrase.endsWith(" ")) {
            System.out.println("WARNING: Passphrase has leading or trailing spaces!");
        }
        
        System.out.println("\nNote: Coinbase Exchange API passphrases are case-sensitive.");
        System.out.println("Make sure the passphrase matches exactly what was shown when you created the API key.");
    }
}