package com.tashin.physicsai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiConfig.class);

    @Bean
    public OpenAPI physicsAiOpenAPI() {

        logger.info("Configuring OpenAPI / Swagger documentation");

        return new OpenAPI()
                .info(new Info()
                        .title("Physics AI Tutor API")
                        .description("REST API for the Physics AI Tutor Learning Management System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tashin Kavishan")
                                .email("tashin@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ));
    }
}
