package com.tashin.physicsai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${python.ai.base-url}")
    private String pythonBaseUrl;

    // ── Python AI (WebClient / WebFlux) ──────────────────────────────────────

    @Bean
    public WebClient pythonWebClient() {

        return WebClient.builder()
                .baseUrl(pythonBaseUrl)
                .build();

    }

    // ── WhatsApp Cloud API (RestClient – Spring Boot 3.2+) ───────────────────

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

}
