package com.bangchef.recipe_platform.recipe.dto;

import com.bangchef.recipe_platform.common.enums.RecipeCategory;
import com.bangchef.recipe_platform.common.enums.RecipeSortType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesBySortDto {
    private RecipeCategory[] categories;
    private RecipeSortType sortType;
}
