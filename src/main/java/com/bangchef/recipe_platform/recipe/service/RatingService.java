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

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RecipeRepository recipeRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RecipeService recipeService;

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

        updateRecipeAvgRating(recipe);
        recipeService.calculateOverallScore();

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

        updateRecipeAvgRating(recipeRepository.findById(requestDto.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found")));

        recipeService.calculateOverallScore();

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
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        Rating rating = ratingRepository.findByUser_IdAndRecipe_Id(userId, recipeId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        ratingRepository.delete(rating);

        updateRecipeAvgRating(recipe);

        recipeService.calculateOverallScore();
    }

    @Transactional
    private void updateRecipeAvgRating(Recipe recipe) {
        List<Rating> ratingList = ratingRepository.findByRecipe_Id(recipe.getId());
        double avgRating = ratingList.stream()
                .mapToDouble(Rating::getRating)
                .average()
                .orElse(0.0);

        recipe.setAvgRating(Math.round(avgRating * 100) / 100.0f);
        recipeRepository.save(recipe);
    }
}
