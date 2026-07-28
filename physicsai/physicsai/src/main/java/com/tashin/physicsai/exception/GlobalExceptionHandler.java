package com.tashin.physicsai.exception;

import com.tashin.physicsai.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleNotFound(
                        ResourceNotFoundException ex,
                        HttpServletRequest request) {

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error(HttpStatus.NOT_FOUND.name())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

        }

        @ExceptionHandler(BadRequestException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleBadRequest(
                        BadRequestException ex,
                        HttpServletRequest request) {

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.name())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

        }

        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse handleException(
                        Exception ex,
                        HttpServletRequest request) {

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();
        }

        @ExceptionHandler(WebhookVerificationException.class)
        public ResponseEntity<String> handleWebhookVerification(
                        WebhookVerificationException ex) {

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ex.getMessage());
        }

}
