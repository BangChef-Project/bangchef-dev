package com.bangchef.recipe_platform.common.enums;

public enum RequestStatus {
    PENDING, APPROVED, REJECTED;

    // 상태가 유효한지 확인하는 메서드
    public boolean isValid() {
        return this == PENDING || this == APPROVED || this == REJECTED;
    }
}