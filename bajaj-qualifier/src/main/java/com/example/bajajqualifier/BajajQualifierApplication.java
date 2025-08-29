package com.example.bajajqualifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BajajQualifierApplication {

    public static void main(String[] args) {
        SpringApplication.run(BajajQualifierApplication.class, args);
    }

    // This bean is required for making API calls
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}