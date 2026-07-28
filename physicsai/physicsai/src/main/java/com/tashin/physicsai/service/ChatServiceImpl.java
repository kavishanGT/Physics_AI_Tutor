package com.tashin.physicsai.service;

import com.tashin.physicsai.client.PythonAIClient;
import com.tashin.physicsai.dto.request.ChatRequest;
import com.tashin.physicsai.dto.response.ChatResponse;
import com.tashin.physicsai.dto.response.ConversationMessage;
import com.tashin.physicsai.entity.Conversation;
import com.tashin.physicsai.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

        private final PythonAIClient pythonAIClient;
        private final UserService userService;
        private final ConversationService conversationService;
        private final MessageService messageService;

        @Override
        @Transactional
        public ChatResponse ask(Conversation conversation, ChatRequest request) {

                // 3. Build history from the last 10 messages (fetched BEFORE saving current
                // question)
                List<ConversationMessage> history = messageService
                                .getRecentMessages(conversation.getId(), 10)
                                .stream()
                                .map(message -> new ConversationMessage(
                                                message.getRole().name(),
                                                message.getContent()))
                                .toList();

                // 5. Call Python AI with history included
                ChatRequest requestWithHistory = new ChatRequest(
                                request.phoneNumber(),
                                request.displayName(),
                                request.question(),
                                history);

                ChatResponse response = pythonAIClient.ask(requestWithHistory);

                // 6. Persist the AI's answer
                messageService.saveAssistantMessage(conversation, response.answer());

                log.info("Python AI responded successfully.");

                return response;
        }

}
