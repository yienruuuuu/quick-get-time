package com.example.quickgettime;

import com.example.quickgettime.service.GetTimeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuickGetTimeApplication {
    private final GetTimeService getTimeService;

    public QuickGetTimeApplication(GetTimeService getTimeService) {
        this.getTimeService = getTimeService;
    }


    public static void main(String[] args) {
        SpringApplication.run(QuickGetTimeApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("發出 HTTP Request...");
            getTimeService.getTime();
        };
    }
}