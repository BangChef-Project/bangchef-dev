package com.bangchef.recipe_platform.recipe.repository;

import com.bangchef.recipe_platform.common.enums.RecipeCategory;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import com.bangchef.recipe_platform.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    void deleteByUser(User user);

    Page<Recipe> findAllByOrderByOverallScoreDesc(Pageable pageable); // 종합점수 순 정렬

    Page<Recipe> findAllByOrderByCommentsCountDesc(Pageable pageable); // 댓글많은 순 정렬

    Page<Recipe> findAllByOrderByViewsDesc(Pageable pageable); // 조회수 순 정렬

    Page<Recipe> findAllByOrderByAvgRatingDesc(Pageable pageable); // 평균별점 순 정렬

    Page<Recipe> findAllByOrderByFavoritesCountDesc(Pageable pageable); // 즐겨찾기수 순 정렬
}