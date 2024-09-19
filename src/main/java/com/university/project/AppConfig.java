package com.university.project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    // OpenAI use
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
