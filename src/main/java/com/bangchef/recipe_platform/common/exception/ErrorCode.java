package com.bangchef.recipe_platform.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //일반
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),



    // 유저 관련
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 로그인된 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    ALREADY_LOGGED_IN(HttpStatus.CONFLICT, "이미 로그인된 사용자입니다. 로그아웃 후 다시 시도해주세요."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 메일 인증 관련
    EMAIL_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "메일 인증에 실패했습니다."),
    EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증된 이메일입니다."),
    EMAIL_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),

    // 임시 비밀번호 재발급 관련
    PASSWORD_RESET_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 재설정 중 오류가 발생했습니다."),
    USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일로 등록된 사용자를 찾을 수 없습니다."),
    TEMP_PASSWORD_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "임시 비밀번호 생성에 실패했습니다.");




    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
