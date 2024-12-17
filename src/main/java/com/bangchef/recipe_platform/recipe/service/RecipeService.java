package com.bangchef.recipe_platform.recipe.service;

import com.bangchef.recipe_platform.recipe.dto.RequestRecipeDto;
import com.bangchef.recipe_platform.recipe.dto.ResponseRecipeDto;
import com.bangchef.recipe_platform.recipe.entity.CookingStep;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import com.bangchef.recipe_platform.recipe.repository.CookingStepRepository;
import com.bangchef.recipe_platform.recipe.repository.RecipeRepository;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CookingStepRepository cookingStepRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseRecipeDto.RecipeInfo createRecipe(RequestRecipeDto.Create requestDto, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Recipe recipe = Recipe.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .ingredients(requestDto.getIngredients())
                .category(requestDto.getCategory())
                .difficulty(requestDto.getDifficulty())
                .cookTime(requestDto.getCookTime())
                .imageUrl(requestDto.getImageUrl())
                .user(user)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);

        List<CookingStep> cookingStepList = requestDto.getCookingStepDtoList().stream()
                .map(cookingStepDto -> CookingStep.builder()
                        .recipe(savedRecipe)
                        .stepNumber(cookingStepDto.getStepNumber())
                        .description(cookingStepDto.getDescription())
                        .imageUrl(cookingStepDto.getImageUrl())
                        .build())
                .toList();

        savedRecipe.setCookingStepList(cookingStepList);

        cookingStepRepository.saveAll(cookingStepList);

        return ResponseRecipeDto.RecipeInfo.builder()
                .username(user.getUsername())
                .id(savedRecipe.getId())
                .title(savedRecipe.getTitle())
                .description(savedRecipe.getDescription())
                .ingredients(savedRecipe.getIngredients())
                .category(savedRecipe.getCategory())
                .difficulty(savedRecipe.getDifficulty())
                .cookTime(savedRecipe.getCookTime())
                .views(savedRecipe.getViews())
                .favoritesCount(savedRecipe.getFavoritesCount())
                .avgRating(savedRecipe.getAvgRating())
                .imageUrl(savedRecipe.getImageUrl())
                .createdAt(savedRecipe.getCreatedAt())
                .cookingStepList(requestDto.getCookingStepDtoList())
                .build();
    }

    @Transactional
    public ResponseRecipeDto.RecipeInfo updateRecipe(RequestRecipeDto.Update requestDto) {
        Recipe recipe = recipeRepository.findById(requestDto.getId()).orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipe.setTitle(requestDto.getTitle());
        recipe.setDescription(requestDto.getDescription());
        recipe.setIngredients(requestDto.getIngredients());
        recipe.setCategory(requestDto.getCategory());
        recipe.setDifficulty(requestDto.getDifficulty());
        recipe.setCookTime(requestDto.getCookTime());
        recipe.setImageUrl(requestDto.getImageUrl());

        cookingStepRepository.deleteByRecipeId(recipe.getId());

        List<CookingStep> cookingStepList = requestDto.getCookingStepDtoList().stream()
                .map(cookingStepDto -> CookingStep.builder()
                        .recipe(recipe)
                        .stepNumber(cookingStepDto.getStepNumber())
                        .description(cookingStepDto.getDescription())
                        .imageUrl(cookingStepDto.getImageUrl())
                        .build())
                .toList();

        cookingStepRepository.saveAll(cookingStepList);

        return ResponseRecipeDto.RecipeInfo.builder()
                .username(recipe.getUser().getUsername())
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .ingredients(recipe.getIngredients())
                .category(recipe.getCategory())
                .difficulty(recipe.getDifficulty())
                .cookTime(recipe.getCookTime())
                .views(recipe.getViews())
                .favoritesCount(recipe.getFavoritesCount())
                .avgRating(recipe.getAvgRating())
                .imageUrl(recipe.getImageUrl())
                .createdAt(recipe.getCreatedAt())
                .cookingStepList(requestDto.getCookingStepDtoList())
                .build();
    }

    @Transactional
    public void deleteRecipe(Long recipeId) {
        recipeRepository.deleteById(recipeId);
    }
}