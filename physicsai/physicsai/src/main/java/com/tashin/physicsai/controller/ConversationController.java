package com.tashin.physicsai.controller;

import com.tashin.physicsai.dto.response.ApiResponse;
import com.tashin.physicsai.dto.response.ConversationResponse;
import com.tashin.physicsai.dto.response.MessageResponse;
import com.tashin.physicsai.entity.Message;
import com.tashin.physicsai.service.ConversationService;
import com.tashin.physicsai.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {

        private final ConversationService conversationService;
        private final MessageService messageService;

        // POST /api/v1/conversations?userId=1&title=NewtonLaws
        @PostMapping
        public ApiResponse<ConversationResponse> createConversation(
                        @RequestParam Long userId,
                        @RequestParam(required = false) String title) {

                ConversationResponse response = conversationService.createConversation(userId, title);

                return ApiResponse.<ConversationResponse>builder()
                                .success(true)
                                .message("Conversation created successfully")
                                .data(response)
                                .build();
        }

        // GET /api/v1/conversations?userId=1
        @GetMapping
        public ApiResponse<List<ConversationResponse>> getConversations(
                        @RequestParam Long userId) {

                List<ConversationResponse> response = conversationService.getConversations(userId);

                return ApiResponse.<List<ConversationResponse>>builder()
                                .success(true)
                                .message("Conversations retrieved successfully")
                                .data(response)
                                .build();
        }

        // GET /api/v1/conversations/{id}/messages
        @GetMapping("/{id}/messages")
        public ApiResponse<List<MessageResponse>> getMessages(
                        @PathVariable Long id) {

                List<MessageResponse> response = messageService
                                .getConversationMessages(id)
                                .stream()
                                .map(this::toMessageResponse)
                                .toList();

                return ApiResponse.success(response);
        }

        // POST /api/v1/conversations/{id}/messages
        @PostMapping("/{id}/messages")
        public ApiResponse<MessageResponse> addMessage(
                        @PathVariable Long id,
                        @RequestParam String role,
                        @RequestParam String content) {

                MessageResponse response = conversationService.addMessage(id, role, content);

                return ApiResponse.<MessageResponse>builder()
                                .success(true)
                                .message("Message added successfully")
                                .data(response)
                                .build();
        }

        // ─── Mapper ───────────────────────────────────────────────────────────────

        private MessageResponse toMessageResponse(Message m) {
                return new MessageResponse(
                                m.getId(),
                                m.getRole().name(),
                                m.getContent(),
                                m.getCreatedAt()
                );
        }
}
