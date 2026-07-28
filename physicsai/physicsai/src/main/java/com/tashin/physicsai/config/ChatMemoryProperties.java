package com.tashin.physicsai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chat.memory")
@Getter
@Setter
public class ChatMemoryProperties {

    /** How many recent messages to include in each AI request. */
    private int recentMessages = 10;

    /** Total message count that triggers the first summary. */
    private int summaryThreshold = 20;

}
