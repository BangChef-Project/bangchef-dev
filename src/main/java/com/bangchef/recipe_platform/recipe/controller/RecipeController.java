package com.bangchef.recipe_platform.recipe.controller;

import com.bangchef.recipe_platform.recipe.dto.RequestRecipeDto;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import com.bangchef.recipe_platform.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<?> createRecipe(@Validated @RequestBody RequestRecipeDto.Create create,
                                          @RequestParam Long userId) {
        Recipe recipe = recipeService.createRecipe(create, userId);

        return ResponseEntity.ok(recipe);
    }
}