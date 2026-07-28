package com.tashin.physicsai.service;

import com.tashin.physicsai.entity.Conversation;
import com.tashin.physicsai.entity.Message;

import java.util.List;

public interface MessageService {

    Message saveUserMessage(
            Conversation conversation,
            String message);

    Message saveAssistantMessage(
            Conversation conversation,
            String message);

    List<Message> getConversationMessages(
            Long conversationId);

    List<Message> getRecentMessages(
            Long conversationId,
            int limit);

}