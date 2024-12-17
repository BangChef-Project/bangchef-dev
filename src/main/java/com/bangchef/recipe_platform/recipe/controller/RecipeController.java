package com.bangchef.recipe_platform.recipe.controller;

import com.bangchef.recipe_platform.common.enums.RecipeSortType;
import com.bangchef.recipe_platform.recipe.dto.RequestRecipeDto;
import com.bangchef.recipe_platform.recipe.service.RecipeService;
import jakarta.validation.Valid;
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

    @GetMapping("/title")
    public ResponseEntity<?> findRecipeByTitle(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortType")RecipeSortType sortType
    ){
        return ResponseEntity.ok(recipeService.findRecipeByTitle(title, page, sortType));
    }

    @GetMapping("/category")
    public ResponseEntity<?> findRecipeByCategory(
            @Valid @RequestBody RequestRecipeDto.CategoriesBySortDto categoriesBySortDto,
            @RequestParam(name = "page", defaultValue = "0") int page
    ){
        return ResponseEntity.ok(recipeService.findRecipeByCategory(categoriesBySortDto.getCategories(), page, categoriesBySortDto.getSortType()));
    }

}