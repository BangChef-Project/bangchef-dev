package com.bangchef.recipe_platform.recipe.dto;

import com.bangchef.recipe_platform.common.enums.Difficulty;
import com.bangchef.recipe_platform.common.enums.RecipeCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseRecipeDto {

    @Data
    @Builder
    public static class RecipeInfo {
        private String username;
        private Long id;
        private String title;
        private String description;
        private String ingredients;
        private RecipeCategory category;
        private Difficulty difficulty;
        private Integer cookTime;
        private Integer views;
        private Integer favoritesCount;
        private Float avgRating;
        private String imageUrl;
        private LocalDateTime createdAt;
        private List<RequestRecipeDto.CookingStepDto> cookingStepList;
    }

}