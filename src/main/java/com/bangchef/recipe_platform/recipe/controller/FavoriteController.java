package com.bangchef.recipe_platform.recipe.controller;

import com.bangchef.recipe_platform.recipe.dto.RequestFavoriteDto;
import com.bangchef.recipe_platform.recipe.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/create")
    public ResponseEntity<?> createFavorite(@RequestBody @Validated RequestFavoriteDto.Create create, Long userId) {
        return ResponseEntity.ok(favoriteService.createFavorite(create, userId));
    }

    @DeleteMapping("/delete/{recipeId}/{userId}")
    public ResponseEntity<?> deleteFavorite(
            @PathVariable Long recipeId, @PathVariable Long userId
    ) {
        favoriteService.deleteFavorite(recipeId, userId);

        return ResponseEntity.ok("즐겨찾기가 삭제되었습니다.");
    }

}
