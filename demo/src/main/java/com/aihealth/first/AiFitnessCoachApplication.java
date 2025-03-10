package com.aihealth.first;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AiFitnessCoachApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiFitnessCoachApplication.class, args);
    }
} 