package com.tashin.physicsai.repository;

import com.tashin.physicsai.entity.Message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    @Query("""
            SELECT m
            FROM Message m
            WHERE m.conversation.id = :conversationId
            ORDER BY m.createdAt DESC
            """)
    List<Message> findRecentMessages(
            @Param("conversationId") Long conversationId,
            Pageable pageable);

    long countByConversationId(Long conversationId);

}
