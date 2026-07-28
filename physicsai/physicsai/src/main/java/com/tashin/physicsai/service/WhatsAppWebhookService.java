package com.tashin.physicsai.service;

import com.tashin.physicsai.dto.request.WebhookRequest;

public interface WhatsAppWebhookService {

        String verifyWebhook(
                        String mode,
                        String verifyToken,
                        String challenge);

        void processWebhook(
                        WebhookRequest request);

}
