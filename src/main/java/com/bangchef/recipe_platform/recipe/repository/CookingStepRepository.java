package com.bangchef.recipe_platform.recipe.repository;

import com.bangchef.recipe_platform.recipe.entity.CookingStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookingStepRepository extends JpaRepository<CookingStep, Long> {
}