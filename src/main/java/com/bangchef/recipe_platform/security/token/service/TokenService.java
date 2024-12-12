package com.bangchef.recipe_platform.security.token.service;

import com.bangchef.recipe_platform.security.JWTUtil;
import com.bangchef.recipe_platform.security.token.entity.RefreshToken;
import com.bangchef.recipe_platform.security.token.repository.RefreshTokenRepository;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TokenService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository,
                        UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refresh = extractRefreshTokenFromCookies(request);

        if (refresh == null) { // 리프레시 토큰이 비어있다면
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 리프레시 토큰 만료 확인
        if (isTokenExpired(refresh)) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인
        if (!isRefreshToken(refresh)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 저장되어 있는지 확인
        if (!refreshTokenRepository.existsByRefresh(refresh)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 유저 정보 확인 및 새로운 토큰 발급
        String email = jwtUtil.getEmail(refresh); // Email 기반으로 변경
        String role = jwtUtil.getRole(refresh);
        User user = userRepository.findByEmail(email) // Email로 사용자 조회
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // 새로운 access 및 refresh 토큰 발급
        String newAccess = jwtUtil.createJwt("access", user, role, 600000L); // 10분
        String newRefresh = jwtUtil.createJwt("refresh", user, role, 604800000L); // 7일

        // 기존 refresh 토큰 삭제 후 새 토큰 저장
        refreshTokenRepository.deleteByEmail(email);
        saveRefreshToken(user, newRefresh, 604800000L); // 새로운 refresh 토큰 저장

        // 갱신된 토큰을 응답으로 전송
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(newAccess, HttpStatus.OK);
    }

    // 쿠키에서 refresh 토큰 추출
    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired(String token) {
        try {
            jwtUtil.isExpired(token);
            return false;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰이 만료되었습니다:" + token);
            return true;
        }
    }

    // refresh 토큰 여부 확인
    private boolean isRefreshToken(String token) {
        return "refresh".equals(jwtUtil.getTokenType(token));
    }

    // Refresh 토큰 저장
    @Transactional
    private void saveRefreshToken(User user, String refreshToken, Long expiredMs) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setRefresh(refreshToken);
        refreshTokenEntity.setExpiration(expirationDate.toString());

        refreshTokenRepository.save(refreshTokenEntity);
    }

    // 쿠키 생성 메서드
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 하루
        cookie.setHttpOnly(true);
        return cookie;
    }
}
