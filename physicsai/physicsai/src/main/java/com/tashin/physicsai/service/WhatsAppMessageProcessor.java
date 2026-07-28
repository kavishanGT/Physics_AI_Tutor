package com.tashin.physicsai.service;

import com.tashin.physicsai.dto.request.WebhookRequest;

public interface WhatsAppMessageProcessor {

    void processIncomingMessage(WebhookRequest request);

}