package com.bangchef.recipe_platform.common.exception;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(
            CustomException e, HttpServletRequest request) {
        log.error("CustomException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(
                e.getErrorCode(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(response);
    }

    @ExceptionHandler(MessagingException.class)
    protected ResponseEntity<ErrorResponse> handleMessagingException(
            MessagingException e, HttpServletRequest request) {
        log.error("MessagingException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.EMAIL_SEND_FAILURE,
                request.getRequestURI()
        );
        return ResponseEntity
                .status(ErrorCode.EMAIL_SEND_FAILURE.getStatus())
                .body(response);
    }

}
