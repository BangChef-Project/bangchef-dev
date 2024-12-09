package com.bangchef.recipe_platform.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().name())
                .message(errorCode.getMessage())
                .path(path)
                .build();
    }
}
