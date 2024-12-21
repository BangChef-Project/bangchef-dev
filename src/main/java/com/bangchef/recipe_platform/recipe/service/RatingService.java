package com.bangchef.recipe_platform.recipe.service;

import com.bangchef.recipe_platform.recipe.dto.RequestRatingDto;
import com.bangchef.recipe_platform.recipe.dto.ResponseRatingDto;
import com.bangchef.recipe_platform.recipe.entity.Rating;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import com.bangchef.recipe_platform.recipe.repository.RatingRepository;
import com.bangchef.recipe_platform.recipe.repository.RecipeRepository;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RecipeRepository recipeRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseRatingDto.Detail createRating(RequestRatingDto.CreateOrUpdate requestDto, Long userId) {
        Recipe recipe = recipeRepository.findById(requestDto.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rating savedRating = ratingRepository.save(Rating.builder()
                .user(user)
                .recipe(recipe)
                .rating(requestDto.getRating())
                .build());

        return ResponseRatingDto.Detail.builder()
                .ratingId(savedRating.getId())
                .recipeId(savedRating.getRecipe().getId())
                .userId(savedRating.getUser().getId())
                .username(savedRating.getUser().getUsername())
                .rating(savedRating.getRating())
                .createdAt(savedRating.getCreatedAt())
                .build();
    }

    @Transactional
    public ResponseRatingDto.Detail updateRating(RequestRatingDto.CreateOrUpdate requestDto) {
        Rating rating = ratingRepository.findById(requestDto.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        rating.setRating(requestDto.getRating());

        return ResponseRatingDto.Detail.builder()
                .ratingId(rating.getId())
                .recipeId(rating.getRecipe().getId())
                .userId(rating.getUser().getId())
                .username(rating.getUser().getUsername())
                .rating(rating.getRating())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteRating(Long recipeId, Long userId) {
        recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        Rating rating = ratingRepository.findByUser_IdAndRecipe_Id(userId, recipeId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        ratingRepository.delete(rating);
    }
}
