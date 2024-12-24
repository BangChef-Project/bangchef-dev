package com.bangchef.recipe_platform.recipe.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class ResponseFavoriteDto {

    @Builder
    @Data
    public static class Detail {

        private Long favoriteId; // 즐겨찾기 ID
        private Long recipeId; // 레시피 ID
        private String recipeTitle; // 레시피 제목
        private Long userId; // 사용자 ID
        private String username; // 사용자 이름
        private LocalDateTime createdAt; // 즐겨찾기 추가 시간
    }
}
