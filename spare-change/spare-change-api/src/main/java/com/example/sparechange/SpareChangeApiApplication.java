package com.example.sparechange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpareChangeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpareChangeApiApplication.class, args);
    }

}