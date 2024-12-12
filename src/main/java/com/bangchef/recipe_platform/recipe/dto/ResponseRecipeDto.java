package com.bangchef.recipe_platform.recipe.dto;

import com.bangchef.recipe_platform.common.enums.Difficulty;
import com.bangchef.recipe_platform.common.enums.RecipeCategory;
import lombok.Data;

public class ResponseRecipeDto {

    @Data
    public static class List {
        private Long id;
        private String title;
        private RecipeCategory category;
        private Difficulty difficulty;
        private int cookTime;
        private int views;
        private int favoritesCount;
        private Float avgRating;
        private String imageUrl;
    }

    @Data
    public static class Detail {
        private Long id;
        private String title;
        private String description;
        private String ingredients;
        private RecipeCategory category;
        private Difficulty difficulty;
        private int cookTime;
        private int views;
        private int favoritesCount;
        private Float avgRating;
        private String imageUrl;
    }

}