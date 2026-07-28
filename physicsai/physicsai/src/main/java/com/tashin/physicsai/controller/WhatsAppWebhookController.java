package com.tashin.physicsai.controller;

import org.springframework.web.bind.annotation.*;

import com.tashin.physicsai.dto.request.WebhookRequest;
import com.tashin.physicsai.service.WhatsAppWebhookService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/whatsapp")
@RequiredArgsConstructor
public class WhatsAppWebhookController {

    private final WhatsAppWebhookService webhookService;

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(

            @RequestParam("hub.mode") String mode,

            @RequestParam("hub.verify_token") String verifyToken,

            @RequestParam("hub.challenge") String challenge) {

        String response = webhookService.verifyWebhook(
                mode,
                verifyToken,
                challenge);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveWebhook(
            @RequestBody WebhookRequest request) {

        webhookService.processWebhook(request);

        return ResponseEntity.ok().build();
    }

}
