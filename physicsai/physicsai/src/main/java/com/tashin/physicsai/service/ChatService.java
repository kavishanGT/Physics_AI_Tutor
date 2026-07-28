package com.tashin.physicsai.service;

import com.tashin.physicsai.dto.request.ChatRequest;
import com.tashin.physicsai.dto.response.ChatResponse;
import com.tashin.physicsai.entity.Conversation;

import jakarta.validation.Valid;

public interface ChatService {
    ChatResponse ask(Conversation conversation, ChatRequest request);
}
