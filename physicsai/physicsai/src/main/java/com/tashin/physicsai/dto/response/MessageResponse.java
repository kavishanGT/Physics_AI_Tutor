package com.tashin.physicsai.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(

        Long id,

        String role,

        String content,

        LocalDateTime createdAt

) {
}
