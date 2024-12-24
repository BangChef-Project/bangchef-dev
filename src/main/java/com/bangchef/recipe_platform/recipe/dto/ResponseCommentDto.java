package com.bangchef.recipe_platform.recipe.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseCommentDto {

    @Data
    @Builder
    public static class Detail {
        private Long commentId;
        private Long parentId; // null 이면 일반 댓글
        private Long recipeId;
        private Long userId;
        private String username;
        private String content;
        private LocalDateTime createdAt;
    }
}
