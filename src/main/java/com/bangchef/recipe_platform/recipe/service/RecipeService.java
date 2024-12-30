package com.bangchef.recipe_platform.recipe.service;

import com.bangchef.recipe_platform.common.enums.RecipeCategory;
import com.bangchef.recipe_platform.common.enums.RecipeSortType;
import com.bangchef.recipe_platform.common.exception.CustomException;
import com.bangchef.recipe_platform.common.exception.ErrorCode;
import com.bangchef.recipe_platform.fcm.dto.RequestFCMDto;
import com.bangchef.recipe_platform.fcm.service.FirebaseCloudMessageService;
import com.bangchef.recipe_platform.recipe.dto.RequestRecipeDto;
import com.bangchef.recipe_platform.recipe.dto.ResponseRecipeDto;
import com.bangchef.recipe_platform.recipe.entity.CookingStep;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import com.bangchef.recipe_platform.recipe.repository.CookingStepRepository;
import com.bangchef.recipe_platform.recipe.repository.RecipeRepository;
import com.bangchef.recipe_platform.redis.service.RedisService;
import com.bangchef.recipe_platform.user.entity.Subscription;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.SubscriptionRepository;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CookingStepRepository cookingStepRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final RedisService redisService;

    @Transactional
    public ResponseRecipeDto.RecipeInfo getRecipeDetail(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipe.setViews(recipe.getViews() + 1); // 조회수 증가

        calculateOverallScore();

        return ResponseRecipeDto.RecipeInfo.builder()
                .username(recipe.getUser().getUsername())
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .ingredients(recipe.getIngredients())
                .category(recipe.getCategory())
                .difficulty(recipe.getDifficulty())
                .cookTime(recipe.getCookTime())
                .views(recipe.getViews())
                .favoritesCount(recipe.getFavoritesCount())
                .avgRating(recipe.getAvgRating())
                .imageUrl(recipe.getImageUrl())
                .createdAt(recipe.getCreatedAt())
                .cookingStepList(recipe.getCookingStepList().stream()
                        .map(cookingStep -> RequestRecipeDto.CookingStepDto.builder()
                                .stepNumber(cookingStep.getStepNumber())
                                .description(cookingStep.getDescription())
                                .imageUrl(cookingStep.getImageUrl())
                                .build())
                        .toList())
                .build();
    }

    @Transactional
    public ResponseRecipeDto.RecipeInfo createRecipe(RequestRecipeDto.Create requestDto, Long userId) throws IOException {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        user.setRecipeCount(user.getRecipeCount() + 1);
        redisService.addRecipeCountData(user);
        redisService.addScoreData(user);
        userRepository.save(user);

        Recipe recipe = Recipe.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .ingredients(requestDto.getIngredients())
                .category(requestDto.getCategory())
                .difficulty(requestDto.getDifficulty())
                .cookTime(requestDto.getCookTime())
                .imageUrl(requestDto.getImageUrl())
                .user(user)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);

        List<CookingStep> cookingStepList = requestDto.getCookingStepDtoList().stream()
                .map(cookingStepDto -> CookingStep.builder()
                        .recipe(savedRecipe)
                        .stepNumber(cookingStepDto.getStepNumber())
                        .description(cookingStepDto.getDescription())
                        .imageUrl(cookingStepDto.getImageUrl())
                        .build())
                .toList();

        savedRecipe.setCookingStepList(cookingStepList);

        cookingStepRepository.saveAll(cookingStepList);

        List<Subscription> subscriptionList = subscriptionRepository.findByUserEmail(user.getEmail());

        RequestFCMDto requestFCMDto = RequestFCMDto.builder()
                .title(user.getUsername() + "님의 새 레시피가 등록되었습니다!")
                .body("레시피 제목 : " + savedRecipe.getTitle())
                .build();

        for (Subscription subscription : subscriptionList){
            User trg = subscription.getSubscriber();
            firebaseCloudMessageService.sendMessageTo(
                    trg.getFcmToken(),
                    requestFCMDto.getTitle(),
                    requestFCMDto.getBody()
            );
        }

        return ResponseRecipeDto.RecipeInfo.builder()
                .username(user.getUsername())
                .id(savedRecipe.getId())
                .title(savedRecipe.getTitle())
                .description(savedRecipe.getDescription())
                .ingredients(savedRecipe.getIngredients())
                .category(savedRecipe.getCategory())
                .difficulty(savedRecipe.getDifficulty())
                .cookTime(savedRecipe.getCookTime())
                .views(savedRecipe.getViews())
                .favoritesCount(savedRecipe.getFavoritesCount())
                .avgRating(savedRecipe.getAvgRating())
                .imageUrl(savedRecipe.getImageUrl())
                .createdAt(savedRecipe.getCreatedAt())
                .cookingStepList(requestDto.getCookingStepDtoList())
                .build();
    }

    @Transactional
    public ResponseRecipeDto.RecipeInfo updateRecipe(RequestRecipeDto.Update requestDto) {
        Recipe recipe = recipeRepository.findById(requestDto.getId()).orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipe.setTitle(requestDto.getTitle());
        recipe.setDescription(requestDto.getDescription());
        recipe.setIngredients(requestDto.getIngredients());
        recipe.setCategory(requestDto.getCategory());
        recipe.setDifficulty(requestDto.getDifficulty());
        recipe.setCookTime(requestDto.getCookTime());
        recipe.setImageUrl(requestDto.getImageUrl());

        cookingStepRepository.deleteByRecipe_Id(recipe.getId());

        List<CookingStep> cookingStepList = requestDto.getCookingStepDtoList().stream()
                .map(cookingStepDto -> CookingStep.builder()
                        .recipe(recipe)
                        .stepNumber(cookingStepDto.getStepNumber())
                        .description(cookingStepDto.getDescription())
                        .imageUrl(cookingStepDto.getImageUrl())
                        .build())
                .toList();

        cookingStepRepository.saveAll(cookingStepList);

        return ResponseRecipeDto.RecipeInfo.builder()
                .username(recipe.getUser().getUsername())
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .ingredients(recipe.getIngredients())
                .category(recipe.getCategory())
                .difficulty(recipe.getDifficulty())
                .cookTime(recipe.getCookTime())
                .views(recipe.getViews())
                .favoritesCount(recipe.getFavoritesCount())
                .avgRating(recipe.getAvgRating())
                .imageUrl(recipe.getImageUrl())
                .createdAt(recipe.getCreatedAt())
                .cookingStepList(requestDto.getCookingStepDtoList())
                .build();
    }

    @Transactional
    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));

        User user = userRepository.findById(recipe.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));

        user.setRecipeCount(user.getRecipeCount() - 1);
        redisService.addRecipeCountData(user);
        redisService.addScoreData(user);
        userRepository.save(user);

        recipeRepository.deleteById(recipeId);
    }

    private ResponseRecipeDto.Detail convertToRecipeResponseDTO(Recipe recipe) {
        ResponseRecipeDto.Detail detail = new ResponseRecipeDto.Detail();

        detail.setId(recipe.getId());
        detail.setTitle(recipe.getTitle());
        detail.setCategory(recipe.getCategory());
        detail.setDifficulty(recipe.getDifficulty());
        detail.setCookTime(recipe.getCookTime());
        detail.setViews(recipe.getViews());
        detail.setFavoritesCount(recipe.getFavoritesCount());
        detail.setAvgRating(recipe.getAvgRating());
        detail.setImageUrl(recipe.getImageUrl());

        return detail;
    }

    // 관리자 특정 레시피 삭제 권한
    @Transactional
    public void deleteRecipeById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
        recipeRepository.delete(recipe);
    }

    // 관리자 특정 회원 레시피 일괄 삭제
    @Transactional
    public void deleteRecipesByUser(User user) {
        recipeRepository.deleteByUser(user);
    }

    public List<ResponseRecipeDto.Detail> findRecipeByTitle(String title, int page, RecipeSortType sortType) {
        List<Recipe> recipeList = recipeRepository.findByTitle(title);

        if (recipeList.isEmpty()) {
            throw new CustomException(ErrorCode.RECIPE_NOT_FOUND);
        }

        List<ResponseRecipeDto.Detail> recipeDtoList = new ArrayList<>();

        for (Recipe recipe : recipeList) {
            ResponseRecipeDto.Detail recipeDto = convertToRecipeResponseDTO(recipe);
            recipeDtoList.add(recipeDto);
        }

        sortBySortType(recipeDtoList, sortType);

        return getPagedRecipe(recipeDtoList, page);
    }

    public List<ResponseRecipeDto.Detail> findRecipeByCategory(RecipeCategory[] categories, int page, RecipeSortType sortType) {
        HashMap<String, Integer> countingMap = new HashMap<>();
        Set<Recipe> recipeSet = new HashSet<>();

        if (categories.length == 0) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        for (RecipeCategory category : categories) {
            List<Recipe> recipeList = recipeRepository.findByCategory(category);

            if (recipeList.isEmpty()) {
                throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
            }

            recipeSet.addAll(recipeList);

            for (Recipe recipe : recipeList) {
                String title = recipe.getTitle();
                countingMap.put(title, countingMap.getOrDefault(title, 0) + 1);
            }
        }

        List<ResponseRecipeDto.Detail> recipeDtoList = new ArrayList<>();

        for (Recipe recipe : recipeSet) {
            String title = recipe.getTitle();

            if (countingMap.containsKey(title) && countingMap.get(title) > 0) {
                ResponseRecipeDto.Detail recipeDto = convertToRecipeResponseDTO(recipe);
                recipeDtoList.add(recipeDto);
            }
        }

        sortBySortType(recipeDtoList, sortType);

        return getPagedRecipe(recipeDtoList, page);
    }

    private static void sortBySortType(List<ResponseRecipeDto.Detail> recipeDtoList, RecipeSortType sortType) {
        if (sortType == RecipeSortType.VIEWS_ASC) {
            recipeDtoList.sort(new Comparator<ResponseRecipeDto.Detail>() {
                @Override
                public int compare(ResponseRecipeDto.Detail o1, ResponseRecipeDto.Detail o2) {
                    return o1.getViews() - o2.getViews();
                }
            });
        } else if (sortType == RecipeSortType.VIEWS_DES) {
            recipeDtoList.sort(new Comparator<ResponseRecipeDto.Detail>() {
                @Override
                public int compare(ResponseRecipeDto.Detail o1, ResponseRecipeDto.Detail o2) {
                    return o2.getViews() - o1.getViews();
                }
            });
        } else if (sortType == RecipeSortType.RATING_ASC) {
            recipeDtoList.sort(new Comparator<ResponseRecipeDto.Detail>() {
                @Override
                public int compare(ResponseRecipeDto.Detail o1, ResponseRecipeDto.Detail o2) {
                    return (int) (o1.getAvgRating() - o2.getAvgRating());
                }
            });
        } else if (sortType == RecipeSortType.RATING_DES) {
            recipeDtoList.sort(new Comparator<ResponseRecipeDto.Detail>() {
                @Override
                public int compare(ResponseRecipeDto.Detail o1, ResponseRecipeDto.Detail o2) {
                    return (int) (o2.getViews() - o1.getViews());
                }
            });
        } else if (sortType == RecipeSortType.FAVORITES_ASC) {
            recipeDtoList.sort(new Comparator<ResponseRecipeDto.Detail>() {
                @Override
                public int compare(ResponseRecipeDto.Detail o1, ResponseRecipeDto.Detail o2) {
                    return o1.getFavoritesCount() - o2.getFavoritesCount();
                }
            });
        } else if (sortType == RecipeSortType.FAVORITES_DES) {
            recipeDtoList.sort(new Comparator<ResponseRecipeDto.Detail>() {
                @Override
                public int compare(ResponseRecipeDto.Detail o1, ResponseRecipeDto.Detail o2) {
                    return o2.getFavoritesCount() - o1.getFavoritesCount();
                }
            });
        }
    }

    private static List<ResponseRecipeDto.Detail> getPagedRecipe(List<ResponseRecipeDto.Detail> recipeDtoList, int page) {
        List<ResponseRecipeDto.Detail> pagedRecipe = new ArrayList<>();
        final int PAGE_SIZE = 15;

        for (int i = PAGE_SIZE * page; i < PAGE_SIZE * (page + 1); i++) {
            if (recipeDtoList.size() <= i) {
                break;
            }

            pagedRecipe.add(recipeDtoList.get(i));
        }

        return pagedRecipe;
    }

    @Transactional
    public Page<ResponseRecipeDto.Ranking> getRecipeRanking(RequestRecipeDto.Ranking ranking) {
        Pageable pageable = PageRequest.of(ranking.getPage(), ranking.getSize());

        Page<Recipe> recipePage = switch (ranking.getCriteria()) {
            case OVERALL -> recipeRepository.findAllByOrderByOverallScoreDesc(pageable);
            case VIEWS -> recipeRepository.findAllByOrderByViewsDesc(pageable);
            case COMMENTS_COUNT -> recipeRepository.findAllByOrderByCommentsCountDesc(pageable);
            case AVG_RATING -> recipeRepository.findAllByOrderByAvgRatingDesc(pageable);
            case FAVORITES_COUNT -> recipeRepository.findAllByOrderByFavoritesCountDesc(pageable);
            default -> throw new IllegalArgumentException("Invalid ranking criteria: " + ranking.getCriteria());
        };

        return recipePage.map(recipe -> ResponseRecipeDto.Ranking.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .username(recipe.getUser().getUsername())
                .views(recipe.getViews())
                .favoritesCount(recipe.getFavoritesCount())
                .commentsCount(recipe.getCommentsCount())
                .avgRating(recipe.getAvgRating())
                .overallScore(recipe.getOverallScore())
                .build());

    }

    // 종합점수 저장
    @Transactional
    public void calculateOverallScore() {

        // 즐겨찾기 순위 가져오기
        Page<Recipe> favoritesPage = recipeRepository.findAllByOrderByFavoritesCountDesc(PageRequest.of(0, (int) recipeRepository.count()));
        List<Recipe> favoritesList = favoritesPage.getContent();
        Map<Long, Integer> favoritesRankMap = getRankMap(favoritesList);

        // 별점 순위 가져오기
        Page<Recipe> ratingPage = recipeRepository.findAllByOrderByAvgRatingDesc(PageRequest.of(0, (int) recipeRepository.count()));
        List<Recipe> ratingList = ratingPage.getContent();
        Map<Long, Integer> ratingRankMap = getRankMap(ratingList);

        // 조회수 순위 가져오기
        Page<Recipe> viewsPage = recipeRepository.findAllByOrderByViewsDesc(PageRequest.of(0, (int) recipeRepository.count()));
        List<Recipe> viewsList = viewsPage.getContent();
        Map<Long, Integer> viewsRankMap = getRankMap(viewsList);

        Map<Long, Long> ratingMap = new HashMap<>();
        Map<Long, Integer> countMap = new HashMap<>();

        for (Recipe recipe : viewsList) {
            long viewsScore = recipeRepository.count() - (viewsRankMap.get(recipe.getId()) - 1);
            long favoritesScore = recipeRepository.count() - (favoritesRankMap.get(recipe.getId()) - 1);
            long ratingScore = recipeRepository.count() - (ratingRankMap.get(recipe.getId()) - 1);

            double overallScore = (viewsScore * 0.33) + (favoritesScore * 0.33) + (ratingScore * 0.33);

            Long userId = recipe.getUser().getId();
            ratingMap.put(userId, (long)ratingRankMap.get(recipe.getId()));
            countMap.put(userId, countMap.getOrDefault(userId, 0) + 1);

            recipe.setOverallScore(overallScore);
            recipeRepository.save(recipe);
        }

        for (Map.Entry<Long, Long> ratingEntry : ratingMap.entrySet()) {
            long userId = ratingEntry.getKey();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            float rating = ratingEntry.getValue() / (float) countMap.get(userId);
            user.setAvgRating(rating);
            redisService.addAvgRatingData(user);
            redisService.addScoreData(user);
        }
    }

    private Map<Long, Integer> getRankMap(List<Recipe> recipes) {
        Map<Long, Integer> rankMap = new HashMap<>();
        for (int i = 0; i < recipes.size(); i++) {
            rankMap.put(recipes.get(i).getId(), i + 1); // 1부터 시작하는 순위
        }
        return rankMap;
    }

}