package com.bangchef.recipe_platform.recipe.repository;

import com.bangchef.recipe_platform.recipe.entity.CookingStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CookingStepRepository extends JpaRepository<CookingStep, Long> {

    List<CookingStep> findByRecipeId(Long recipeId);
    void deleteByRecipeId(Long recipeId);
}