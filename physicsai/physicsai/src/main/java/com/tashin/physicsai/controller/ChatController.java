package com.tashin.physicsai.controller;

import com.tashin.physicsai.dto.request.ChatRequest;
import com.tashin.physicsai.dto.response.ApiResponse;
import com.tashin.physicsai.dto.response.ChatResponse;
import com.tashin.physicsai.entity.Conversation;
import com.tashin.physicsai.entity.User;
import com.tashin.physicsai.service.ChatService;
import com.tashin.physicsai.service.ConversationService;
import com.tashin.physicsai.service.MessageService;
import com.tashin.physicsai.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final ConversationService conversationService;
    private final MessageService messageService;

    @PostMapping
    public ApiResponse<ChatResponse> ask(
            @Valid
            @RequestBody
            ChatRequest request
    ) {
        // 1. Resolve user
        User user = userService.findOrCreateUser(
                request.phoneNumber(), request.displayName());

        // 2. Resolve active conversation
        Conversation conversation = conversationService.resolveConversation(user);

        // 3. Save the user's question (before calling AI so history order is correct)
        messageService.saveUserMessage(conversation, request.question());

        // 4. ChatServiceImpl fetches history, calls Python AI, saves AI answer
        ChatResponse response = chatService.ask(conversation, request);

        return ApiResponse.<ChatResponse>builder()
                .success(true)
                .message("AI response generated successfully")
                .data(response)
                .build();
    }

}
