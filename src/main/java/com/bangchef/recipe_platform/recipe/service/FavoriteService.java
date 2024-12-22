package com.bangchef.recipe_platform.recipe.service;

import com.bangchef.recipe_platform.recipe.dto.RequestFavoriteDto;
import com.bangchef.recipe_platform.recipe.dto.ResponseFavoriteDto;
import com.bangchef.recipe_platform.recipe.entity.Favorite;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import com.bangchef.recipe_platform.recipe.repository.FavoriteRepository;
import com.bangchef.recipe_platform.recipe.repository.RecipeRepository;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public ResponseFavoriteDto.Detail createFavorite(RequestFavoriteDto.Create requestDto, Long userId) {
        Recipe recipe = recipeRepository.findById(requestDto.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Favorite favorite = Favorite.builder()
                .user(user)
                .recipe(recipe)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);

        recipe.setFavoritesCount(recipe.getFavoritesCount() + 1);
        recipeRepository.save(recipe);

        return ResponseFavoriteDto.Detail.builder()
                .favoriteId(savedFavorite.getId())
                .recipeId(savedFavorite.getRecipe().getId())
                .recipeTitle(savedFavorite.getRecipe().getTitle())
                .userId(savedFavorite.getUser().getId())
                .username(savedFavorite.getUser().getUsername())
                .createdAt(savedFavorite.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteFavorite(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        Favorite favorite = favoriteRepository.findByUser_IdAndRecipe_Id(userId, recipeId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        favoriteRepository.delete(favorite);

        recipe.setFavoritesCount(Math.max(0, recipe.getFavoritesCount() - 1));
        recipeRepository.save(recipe);
    }
}
