package com.tashin.physicsai.service;

import org.springframework.stereotype.Service;

import com.tashin.physicsai.config.WhatsappProperties;
import com.tashin.physicsai.dto.request.WebhookRequest;
import com.tashin.physicsai.exception.WebhookVerificationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tashin.physicsai.dto.request.Value;

@Slf4j

@Service
@RequiredArgsConstructor
public class WhatsAppWebhookServiceImpl
        implements WhatsAppWebhookService {

    private final WhatsappProperties properties;
    private final WhatsAppMessageProcessor processor;

    @Override
    public String verifyWebhook(
            String mode,
            String verifyToken,
            String challenge) {

        if (!"subscribe".equals(mode)) {
            throw new WebhookVerificationException("Invalid hub.mode");
        }

        if (!properties.getVerifyToken().equals(verifyToken)) {
            throw new WebhookVerificationException("Invalid verify token");
        }

        return challenge;
    }

    @Override
    public void processWebhook(
            WebhookRequest request) {

        processor.processIncomingMessage(request);

        // if (request.getEntry() == null) {
        // return;
        // }

        // request.getEntry().forEach(entry ->

        // entry.getChanges().forEach(change -> {

        // Value value = change.getValue();

        // if (value.getMessages() == null) {
        // return;
        // }

        // value.getMessages().forEach(message -> {

        // log.info("Sender : {}", message.getFrom());

        // log.info("Message : {}",
        // message.getText().getBody());

        // });

        // })

        // );

    }
}
