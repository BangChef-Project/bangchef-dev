package com.bangchef.recipe_platform.recipe.controller;

import com.bangchef.recipe_platform.recipe.dto.RequestRecipeDto;
import com.bangchef.recipe_platform.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeRatingController {
    private final RecipeService recipeService;

    @PutMapping("/rating")
    public ResponseEntity<?> giveRecipeRating(
            @RequestParam Long recipeId,
            @RequestParam Float rating,
            @RequestHeader("Authorization") String token
    ) throws IOException {
        return ResponseEntity.ok(recipeService.giveRecipeRating(recipeId, rating, token));
    }

    @DeleteMapping("/rating")
    public ResponseEntity<?> removeRecipeRating(
            @RequestParam Long recipeId,
            @RequestHeader("Authorization") String token
    ) throws IOException {
        recipeService.removeRecipeRating(recipeId, token);

        return ResponseEntity.ok("별점이 삭제되었습니다.");
    }
}
