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

    @PostMapping("/create")
    public ResponseEntity<?> createRecipe(@Validated @RequestBody RequestRecipeDto.Create create,
                                          @RequestParam Long userId) {

        return ResponseEntity.ok(recipeService.createRecipe(create, userId));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateRecipe(@Validated @RequestBody RequestRecipeDto.Update update) {

        return ResponseEntity.ok(recipeService.updateRecipe(update));
    }

    @DeleteMapping("/delete/{recipeId}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long recipeId) {

        recipeService.deleteRecipe(recipeId);

        return ResponseEntity.noContent().build();
    }

}