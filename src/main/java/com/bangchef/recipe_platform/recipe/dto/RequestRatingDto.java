package com.bangchef.recipe_platform.recipe.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

public class RequestRatingDto {

    @Data
    public static class CreateOrUpdate {

        @NotNull(message = "레시피 ID는 필수입니다.")
        private Long recipeId; // 별점을 줄 레시피 ID

        @NotNull(message = "별점 점수는 필수입니다.")
        @Min(value = 1, message = "별점은 최소 1이어야 합니다.")
        @Max(value = 10, message = "별점은 최대 10이어야 합니다.")
        private Integer rating; // 별점 점수 (1~10)
    }
}
