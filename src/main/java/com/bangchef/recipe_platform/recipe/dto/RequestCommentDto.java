package com.bangchef.recipe_platform.recipe.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

public class RequestCommentDto {

    @Data
    public static class CreateCommentDto {
        @NotNull(message = "레시피 ID는 필수입니다.")
        private Long recipeId;

        private Long parentId; // null 이면 일반 댓글

        @NotBlank(message = "댓글 내용은 필수입니다.")
        private String content;
    }
}
