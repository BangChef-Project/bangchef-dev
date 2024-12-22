package com.bangchef.recipe_platform.recipe.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class RequestFavoriteDto {

    @Data
    public static class Create {

        @NotNull(message = "레시피 ID는 필수입니다.")
        private Long recipeId; // 즐겨찾기 대상 레시피 ID
    }
}
