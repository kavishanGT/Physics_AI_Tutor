package com.tashin.physicsai.service;

import com.tashin.physicsai.dto.request.Change;
import com.tashin.physicsai.dto.request.ChatRequest;
import com.tashin.physicsai.dto.request.Contact;
import com.tashin.physicsai.dto.request.Entry;
import com.tashin.physicsai.dto.request.Message;
import com.tashin.physicsai.dto.request.Value;
import com.tashin.physicsai.dto.request.WebhookRequest;
import com.tashin.physicsai.dto.response.ChatResponse;
import com.tashin.physicsai.entity.Conversation;
import com.tashin.physicsai.entity.MessageType;
import com.tashin.physicsai.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tashin.physicsai.service.media.WhatsAppMediaService;
import com.tashin.physicsai.service.media.MediaProcessingService;
import java.nio.file.Path;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppMessageProcessorImpl
        implements WhatsAppMessageProcessor {

    private final UserService userService;
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final ChatService chatService;
    private final WhatsAppService whatsAppService;
    private final WhatsAppMediaService mediaService;
    private final MediaProcessingService mediaProcessingService;

    /**
     * Processes the incoming webhook payload asynchronously so that
     * the HTTP thread can return 200 OK to Meta immediately — preventing
     * unnecessary webhook retries caused by slow AI response times.
     */
    @Override
    @Async
    public void processIncomingMessage(WebhookRequest request) {

        for (Entry entry : request.getEntry()) {

            for (Change change : entry.getChanges()) {

                if (!"messages".equals(change.getField())) {
                    continue;
                }

                Value value = change.getValue();

                if (value.getMessages() == null) {
                    continue;
                }

                Contact contact = value.getContacts().get(0);

                for (Message message : value.getMessages()) {

                    MessageType type = MessageType.fromValue(message.getType());

                    try {
                        switch (type) {
                            case TEXT -> handleTextMessage(message, contact);
                            case IMAG dleImageMessage(message, contact);
                            case DOCUM handleDocumentMessage(message, contact);
                            case AUDIO -> dleAudioMessage(message, contact);
                            // case VI handleVideoMessage(message, contact);
                            // case LO -> handleLocationMessage(message, contact);
                            // case INTER VE -> handleInteractiveMessage(message, contact);
                            default -> log.info("Unsupported message type: {}", message.getType());
                        } 
                    } catch (Exception ex) {
                        log.error("Unhandled error processing message from phone={}", message.getFrom(), ex);
                    }

                }

            }

        }

    }

    // ── Text ──────────────────────────────────────────────────────────────────

    private void handleTextMessage(
            Message message,
            Contact contact) {

        String phoneNumber = message.getFrom();
        String displayName = contact.getProfile().getName();
        String question = message.getText().getBody();

        // ── Step 1: Resolve user ──────────────────────────────────────────────
        log.info("Incoming Message → from={} name=\"{}\"", phoneNumber, displayName);

        User user = userService.findOrCreateByPhoneNumber(phoneNumber, displayName);

        log.info("User Found → userId={}", user.getId());

        // ── Step 2: Resolve conversation ──────────────────────────────────────
        Conversation conversation = conversationService.resolveConversation(user);

        log.info("Conversation Resolved → conversationId={}", conversation.getId());

        // ── Step 3: Save the user's incoming message ──────────────────────────
        messageService.saveUserMessage(conversation, question);

        log.info("User Message Saved");

        // ── Step 4: Build ChatRequest and call Python AI ──────────────────────
        log.info("Calling Python AI → question=\"{}\"", question);

        ChatRequest chatRequest = new ChatRequest(
                phoneNumber,
                displayName,
                question,
                null // ChatServiceImpl fetches history from DB
        );

        ChatResponse response;
        try {
            // ChatServiceImpl fetches history, calls AI, and saves the assistant message
            response = chatService.ask(conversation, chatRequest);
        } catch (Exception ex) {
            log.error("Python AI call failed for phone={}", phoneNumber, ex);
            return;
        }

        log.info("AI Response Received → length={} chars", response.answer().length());

        // ── Step 5: Send reply back to WhatsApp ───────────────────────────────
        try {

            whatsAppService.sendTextMessage(user.getPhoneNumber(), response.answer());

            log.info("Reply sent successfully → to={}", phoneNumber);

        } catch (Exception ex) {

            // Do NOT re-throw — we must not bubble up after Meta already got 200 OK
            log.error("Failed to send WhatsApp message to phone={}", phoneNumber, ex);

        }

        log.info("Webhook Completed → phone={}", phoneNumber);

    }

    // ── Placeholder handlers ──────────────────────────────────────────────────

    private void handleImageMessage(
            Message message,
            Contact contact) {

        Path imagePath = mediaService.downloadMedia(
                message.getImage().getId());

        String text = mediaProcessingService.extractText(imagePath, message.getImage().getMimeType());

        log.info("Extracted text from image: {}", text);

        ChatRequest request = new ChatRequest(

                message.getFrom(),
                contact.getProfile().getName(),
                text,
                null);

        ChatResponse response;
        try {
            response = chatService.ask(request);
            log.info("Response: {}", response.answer());
            whatsAppService.sendTextMessage(message.getFrom(), response.answer());
        } catch (Exception ex) {
            log.error("Error processing image message", ex);
        }
    }

    private void handleDocumentMessage(
            Message message,
            Contact contact) {
        Path pdfPath = mediaService.downloadMedia(
                message.getDocument().getId());
        log.info("PDF downloaded: {}", pdfPath);
    }

    private void handleAudioMessage(
            Message message,
            Contact contact) {
        Path audioPath = mediaService.downloadMedia(
                message.getAudio().getId());
        log.info("Audio downloaded: {}", audioPath);
    }

    // private void handleVideoMessage(
    // Message message,
    // Contact contact
    // ) {
    // log.info("Received video.");
    // }

    // private void handleLocationMessage(
    // Message message,
    // Contact contact
    // ) {
    // log.info("Received location.");
    // }

    // private void handleInteractiveMessage(
    // Message message,
    // Contact contact
    // ) {
    // log.info("Received interactive message.");
    // }

}
