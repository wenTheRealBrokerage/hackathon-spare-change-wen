package com.example.sparechange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${spring.web.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD}")
    private String allowedMethods;

    @Value("${spring.web.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${spring.web.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${spring.web.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // Configure allowed origins
        if ("*".equals(allowedOrigins)) {
            corsConfiguration.addAllowedOriginPattern("*");
        } else {
            Arrays.stream(allowedOrigins.split(","))
                    .forEach(corsConfiguration::addAllowedOrigin);
        }
        
        // Configure allowed methods
        Arrays.stream(allowedMethods.split(","))
                .forEach(corsConfiguration::addAllowedMethod);
        
        // Configure allowed headers
        if ("*".equals(allowedHeaders)) {
            corsConfiguration.addAllowedHeader("*");
        } else {
            Arrays.stream(allowedHeaders.split(","))
                    .forEach(corsConfiguration::addAllowedHeader);
        }
        
        // Configure credentials
        corsConfiguration.setAllowCredentials(allowCredentials);
        
        // Configure max age
        corsConfiguration.setMaxAge(maxAge);
        
        // Add exposed headers
        corsConfiguration.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsFilter(source);
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        if ("*".equals(allowedOrigins)) {
            configuration.addAllowedOriginPattern("*");
        } else {
            Arrays.stream(allowedOrigins.split(","))
                    .forEach(configuration::addAllowedOrigin);
        }
        
        Arrays.stream(allowedMethods.split(","))
                .forEach(configuration::addAllowedMethod);
        
        if ("*".equals(allowedHeaders)) {
            configuration.addAllowedHeader("*");
        } else {
            Arrays.stream(allowedHeaders.split(","))
                    .forEach(configuration::addAllowedHeader);
        }
        
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}