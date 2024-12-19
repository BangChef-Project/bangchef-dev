package com.bangchef.recipe_platform.common.exception;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

    // 비밀번호 재발급 관련 에러 처리
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException e, HttpServletRequest request) {
        log.error("RuntimeException: {}", e.getMessage());
        ErrorResponse response;

        if (e.getMessage().contains("사용자")) {
            response = ErrorResponse.of(ErrorCode.USER_EMAIL_NOT_FOUND, request.getRequestURI());
        } else if (e.getMessage().contains("비밀번호")) {
            response = ErrorResponse.of(ErrorCode.PASSWORD_RESET_FAILED, request.getRequestURI());
        } else if (e.getMessage().contains("등업")) { // RoleUpdate 관련 에러 처리
            response = ErrorResponse.of(ErrorCode.ROLE_UPDATE_REQUEST_NOT_FOUND, request.getRequestURI());
        } else {
            response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
        }

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMessage);
    }


}
