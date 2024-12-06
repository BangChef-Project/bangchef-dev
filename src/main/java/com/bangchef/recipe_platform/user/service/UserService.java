package com.bangchef.recipe_platform.user.service;

import com.bangchef.recipe_platform.security.JWTUtil;
import com.bangchef.recipe_platform.security.token.repository.RefreshTokenRepository;
import com.bangchef.recipe_platform.user.dto.LoginDto;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더 추가
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소 추가

    @Value("1")
    private Long jwtExpiration;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;

    }

    // 로그인 로직: DB에서 사용자 정보 조회 후 JWT 발급
    public String loginUser(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 최신 권한을 DB에서 조회하여 토큰 생성
        String role = user.getRole().toString();
        return jwtUtil.createJwt("access", user, role, jwtExpiration);
    }

}
