package com.bangchef.recipe_platform.security.token.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bangchef.recipe_platform.security.token.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/token")
public class ReissueController {
    private final TokenService tokenService;

    public ReissueController(TokenService tokenService) {
        this.tokenService = tokenService;

    }


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<?> result = tokenService.reissueToken(request, response);

        // 토큰이 성공적으로 갱신되었다면  바디엑세스 추가
        if (result.getStatusCode() == HttpStatus.OK) {
            String newAccessToken = (String) result.getBody();
            return ResponseEntity.ok().body(Map.of("access_token", newAccessToken));

        }

        return result;
    }
}
