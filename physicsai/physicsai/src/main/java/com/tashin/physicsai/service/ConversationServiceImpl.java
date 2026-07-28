package com.tashin.physicsai.service;

import com.tashin.physicsai.dto.response.ConversationResponse;
import com.tashin.physicsai.dto.response.MessageResponse;
import com.tashin.physicsai.entity.Conversation;
import com.tashin.physicsai.entity.ConversationStatus;
import com.tashin.physicsai.entity.Message;
import com.tashin.physicsai.entity.MessageRole;
import com.tashin.physicsai.entity.User;
import com.tashin.physicsai.repository.ConversationRepository;
import com.tashin.physicsai.repository.MessageRepository;
import com.tashin.physicsai.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // ─── Core: Get or Create Active Conversation ────────────────────────────────

    @Override
    @Transactional
    public Conversation resolveConversation(User user) {

        log.info("resolveConversation for userId={}", user.getId());

        return conversationRepository
                .findByUserIdAndStatus(user.getId(), ConversationStatus.ACTIVE)
                .orElseGet(() -> {

                    Conversation c = new Conversation();
                    c.setUser(user);
                    c.setStatus(ConversationStatus.ACTIVE);
                    c.setTitle("New Conversation");

                    log.info("Creating new ACTIVE conversation for userId={}", user.getId());
                    return conversationRepository.save(c);
                });
    }

    // ─── List Conversations ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations(Long userId) {

        log.info("Fetching conversations for userId={}", userId);

        return conversationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toConversationResponse)
                .toList();
    }

    // ─── Get Messages ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long conversationId) {

        log.info("Fetching messages for conversationId={}", conversationId);

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    // ─── Add Message ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public MessageResponse addMessage(Long conversationId, String role, String content) {

        log.info("Adding message to conversationId={} role={}", conversationId, role);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found: " + conversationId));

        Message message = new Message();
        message.setConversation(conversation);
        message.setRole(MessageRole.valueOf(role.toUpperCase()));
        message.setContent(content);

        Message saved = messageRepository.save(message);
        return toMessageResponse(saved);
    }

    @Override
    @Transactional
    public ConversationResponse createConversation(Long userId, String title) {

        log.info("Creating conversation for userId={} title={}", userId, title);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        Conversation c = new Conversation();
        c.setUser(user);
        c.setStatus(ConversationStatus.ACTIVE);
        c.setTitle(title != null ? title : "New Conversation");

        return toConversationResponse(conversationRepository.save(c));
    }

    // ─── Mappers ─────────────────────────────────────────────────────────────────

    private ConversationResponse toConversationResponse(Conversation c) {
        return new ConversationResponse(
                c.getId(),
                c.getTitle(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }

    private MessageResponse toMessageResponse(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getRole().name(),   // convert MessageRole enum → String
                m.getContent(),
                m.getCreatedAt());
    }
}
