package com.bangchef.recipe_platform.redis.controller;

import com.bangchef.recipe_platform.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
public class RedisController {
    private final RedisService redisService;
    private final Integer MAX = 10;

    @GetMapping("/score")
    public ResponseEntity<?> getScoreData(){
        return ResponseEntity.ok(redisService.getScoreData(MAX));
    }

    @GetMapping("/subscribe")
    public ResponseEntity<?> getSubscribeData(){
        return ResponseEntity.ok(redisService.getSubscribeData(MAX));
    }

    @GetMapping("/recipe-count")
    public ResponseEntity<?> getRecipeCountData(){
        return ResponseEntity.ok(redisService.getRecipeCountData(MAX));
    }

    @GetMapping("/avg-rating")
    public ResponseEntity<?> getAvgRatingData(){
        return ResponseEntity.ok(redisService.getAvgRatingData(MAX));
    }
}
