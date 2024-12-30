package com.bangchef.recipe_platform.redis.service;

import com.bangchef.recipe_platform.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {
    private static final String SCORE = "score";
    private static final String SUBSCRIBE = "subscribe";
    private static final String RECIPE_COUNT = "recipe_count";
    private static final String AVG_RATING = "average_rating";

    private final RedisTemplate<String, String> redisTemplate;

    private double getScore(int subscribe, int recipe_count, double avg_rating){
        return subscribe * (avg_rating - (avg_rating - 3.0) * Math.pow(2, -Math.log(recipe_count + 1)));
    }

    public void addScoreData(User user){
        redisTemplate.opsForZSet().remove(SCORE, user.getUsername());

        double score = getScore(user.getSubscribers(), user.getRecipeCount(), user.getAvgRating());

        redisTemplate.opsForZSet().add(SCORE, user.getUsername(), score);
    }

    public void addSubscribeData(User user){
        redisTemplate.opsForZSet().remove(SUBSCRIBE, user.getUsername());
        redisTemplate.opsForZSet().add(SUBSCRIBE, user.getUsername(), user.getSubscribers());
    }

    public void addRecipeCountData(User user){
        redisTemplate.opsForZSet().remove(RECIPE_COUNT, user.getUsername());
        redisTemplate.opsForZSet().add(RECIPE_COUNT, user.getUsername(), user.getRecipeCount());
    }

    public void addAvgRatingData(User user){
        redisTemplate.opsForZSet().remove(AVG_RATING, user.getUsername());
        redisTemplate.opsForZSet().add(AVG_RATING, user.getUsername(), user.getAvgRating());
    }

    public Set<String> getScoreData(int count){
        return redisTemplate.opsForZSet().reverseRangeByScore(SCORE, 0, Double.MAX_VALUE, 0, count);
    }

    public Set<String> getSubscribeData(int count){
        return redisTemplate.opsForZSet().reverseRangeByScore(SUBSCRIBE, 0, Double.MAX_VALUE, 0, count);
    }

    public Set<String> getRecipeCountData(int count){
        return redisTemplate.opsForZSet().reverseRangeByScore(RECIPE_COUNT, 0, Double.MAX_VALUE, 0, count);
    }

    public Set<String> getAvgRatingData(int count){
        return redisTemplate.opsForZSet().reverseRangeByScore(AVG_RATING, 0, Double.MAX_VALUE, 0, count);
    }
}
