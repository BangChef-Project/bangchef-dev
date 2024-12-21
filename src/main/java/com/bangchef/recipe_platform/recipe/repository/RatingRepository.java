package com.bangchef.recipe_platform.recipe.repository;

import com.bangchef.recipe_platform.recipe.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
}
