package com.bangchef.recipe_platform.recipe.repository;

import com.bangchef.recipe_platform.common.enums.RecipeCategory;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("SELECT recipe FROM Recipe recipe WHERE recipe.title LIKE %:recipeTitle%")
    List<Recipe> findByTitle(@Param("recipeTitle") String recipeTitle);

    @Query("SELECT recipe FROM Recipe recipe WHERE recipe.category = :category")
    List<Recipe> findByCategory(@Param("category") RecipeCategory category);
}