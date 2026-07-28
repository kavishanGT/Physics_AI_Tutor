package com.tashin.physicsai.repository;

import com.tashin.physicsai.entity.Conversation;
import com.tashin.physicsai.entity.ConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Conversation> findByUserIdAndStatus(Long userId, ConversationStatus status);

}
