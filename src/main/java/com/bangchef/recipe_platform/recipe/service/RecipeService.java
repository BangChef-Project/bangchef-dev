package com.bangchef.recipe_platform.recipe.service;

import com.bangchef.recipe_platform.recipe.dto.RequestRecipeDto;
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
    public Recipe createRecipe(RequestRecipeDto.Create requestDto, Long userId) {

        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));

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

        return savedRecipe;
    }
}