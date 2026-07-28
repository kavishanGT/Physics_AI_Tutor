package com.tashin.physicsai.client;

import com.tashin.physicsai.dto.request.ChatRequest;
import com.tashin.physicsai.dto.response.ChatResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PythonAIClient {

    private final WebClient pythonWebClient;

    public ChatResponse ask(ChatRequest request) {

        return pythonWebClient

                .post()

                .uri("/ask")

                .bodyValue(request)

                .retrieve()

                .bodyToMono(ChatResponse.class)

                .block();

    }

}