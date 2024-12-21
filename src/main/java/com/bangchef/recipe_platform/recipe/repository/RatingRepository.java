package com.bangchef.recipe_platform.recipe.repository;

import com.bangchef.recipe_platform.recipe.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUser_IdAndRecipe_Id(Long userId, Long recipeId);
}
