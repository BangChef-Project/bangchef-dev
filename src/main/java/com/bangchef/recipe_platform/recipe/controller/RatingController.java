package com.bangchef.recipe_platform.recipe.controller;

import com.bangchef.recipe_platform.recipe.dto.RequestRatingDto;
import com.bangchef.recipe_platform.recipe.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/create")
    public ResponseEntity<?> createRating(@RequestBody @Validated RequestRatingDto.CreateOrUpdate createOrUpdate,
                                          @RequestParam Long userId) {
        return ResponseEntity.ok(ratingService.createRating(createOrUpdate, userId));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateRating(@RequestBody @Validated RequestRatingDto.CreateOrUpdate createOrUpdate) {
        return ResponseEntity.ok(ratingService.updateRating(createOrUpdate));
    }

    @DeleteMapping("/delete/{recipeId}/{userId}")
    public ResponseEntity<?> deleteRating(
            @PathVariable Long recipeId, @PathVariable Long userId
    ) {
        ratingService.deleteRating(recipeId, userId);

        return ResponseEntity.ok("별점이 삭제되었습니다.");
    }
}
