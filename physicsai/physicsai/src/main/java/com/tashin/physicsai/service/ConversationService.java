package com.tashin.physicsai.service;

import com.tashin.physicsai.dto.response.ConversationResponse;
import com.tashin.physicsai.dto.response.MessageResponse;
import com.tashin.physicsai.entity.Conversation;
import com.tashin.physicsai.entity.User;

import java.util.List;

public interface ConversationService {

    /**
     * Returns the user's existing ACTIVE conversation,
     * or creates a new one if none exists.
     */
    Conversation resolveConversation(User user);

    List<ConversationResponse> getConversations(Long userId);

    List<MessageResponse> getMessages(Long conversationId);

    MessageResponse addMessage(Long conversationId, String role, String content);

    ConversationResponse createConversation(Long userId, String title);

}
