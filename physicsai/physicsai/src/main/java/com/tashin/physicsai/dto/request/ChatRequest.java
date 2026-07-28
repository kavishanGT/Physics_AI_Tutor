package com.tashin.physicsai.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import com.tashin.physicsai.dto.response.ConversationMessage;

public record ChatRequest(

                String phoneNumber,

                String displayName,

                String question,

                List<ConversationMessage> history

) {
}
