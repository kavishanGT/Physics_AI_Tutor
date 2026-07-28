package com.tashin.physicsai.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.tashin.physicsai.entity.Conversation;

import com.tashin.physicsai.entity.Message;

import com.tashin.physicsai.entity.MessageRole;
import com.tashin.physicsai.repository.MessageRepository;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl
        implements MessageService {

    private final MessageRepository repository;

    @Override
    public Message saveUserMessage(
            Conversation conversation,
            String text) {

        Message message = new Message();

        message.setConversation(conversation);
        message.setRole(MessageRole.USER);
        message.setContent(text);

        return repository.save(message);
    }

    @Override
    public Message saveAssistantMessage(
            Conversation conversation,
            String text) {

        Message message = new Message();

        message.setConversation(conversation);
        message.setRole(MessageRole.ASSISTANT);
        message.setContent(text);

        return repository.save(message);
    }

    @Override
    public List<Message> getConversationMessages(
            Long conversationId) {

        return repository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId);
    }

    @Override
    public List<Message> getRecentMessages(
            Long conversationId,
            int limit) {

        return repository.findRecentMessages(
                conversationId,
                PageRequest.of(0, limit));
    }
}
