package com.tashin.physicsai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "whatsapp.api")
@Getter
@Setter
public class WhatsappProperties {

    private String baseUrl;

    private String version;

    private String phoneNumberId;

    private String accessToken;

    private String verifyToken;

}
