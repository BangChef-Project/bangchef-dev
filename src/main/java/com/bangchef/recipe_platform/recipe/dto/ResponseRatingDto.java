package com.bangchef.recipe_platform.recipe.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class ResponseRatingDto {

    @Data
    @Builder
    public static class Detail {
        private Long ratingId;
        private Long recipeId;
        private Long userId;
        private String username;
        private Integer rating; // 별점 점수 (1~10)
        private LocalDateTime createdAt;
    }
}
