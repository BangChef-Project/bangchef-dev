package com.bangchef.recipe_platform.user.service;

import com.bangchef.recipe_platform.common.exception.CustomException;
import com.bangchef.recipe_platform.common.exception.ErrorCode;
import com.bangchef.recipe_platform.security.JWTUtil;
import com.bangchef.recipe_platform.security.token.repository.RefreshTokenRepository;
import com.bangchef.recipe_platform.user.dto.CustomUserDetails;
import com.bangchef.recipe_platform.user.dto.LoginDto;
import com.bangchef.recipe_platform.user.dto.UserResponseDto;
import com.bangchef.recipe_platform.user.dto.UserUpdateDto;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더 추가
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소 추가
    private final EmailService emailService;

    @Value("1")
    private Long jwtExpiration;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailService = emailService;

    }

    // 로그인 로직: DB에서 사용자 정보 조회 후 JWT 발급
    public String loginUser(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 최신 권한을 DB에서 조회하여 토큰 생성
        String role = user.getRole().toString();
        return jwtUtil.createJwt("access", user, role, jwtExpiration);
    }

    public String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8); // 8자리 임시 비밀번호 생성
    }

    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_EMAIL_NOT_FOUND));

        // 임시 비밀번호 생성
        String tempPassword;
        try {
            tempPassword = generateTempPassword();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TEMP_PASSWORD_GENERATION_FAILED);
        }

        try {
            // 이메일 발송
            emailService.sendTemporaryPasswordEmail(email, tempPassword);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILURE);
        }

        // 비밀번호 암호화 후 저장
        user.setPassword(new BCryptPasswordEncoder().encode(tempPassword));
        userRepository.save(user);
    }

    // 현재 로그인한 사용자 정보 조회
    public UserResponseDto getLoggedInUser() {
        CustomUserDetails userDetails = getLoggedInUserDetails();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToUserResponseDTO(user);
    }

    // 사용자 정보 업데이트
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto) {
        CustomUserDetails userDetails = getLoggedInUserDetails();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userUpdateDto.getUsername() != null) {
            user.setUsername(userUpdateDto.getUsername());
        }
        if (userUpdateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        if (userUpdateDto.getProfileImage() != null) {
            user.setProfileImage(userUpdateDto.getProfileImage());
        }
        if (userUpdateDto.getIntroduction() != null) {
            user.setIntroduction(userUpdateDto.getIntroduction());
        }

        user = userRepository.save(user);
        return convertToUserResponseDTO(user);
    }

    // 현재 로그인한 사용자 정보 가져오기 (SecurityContext 활용)
    private CustomUserDetails getLoggedInUserDetails() {
        return (CustomUserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // User 데이터를 UserResponseDTO로 변환
    private UserResponseDto convertToUserResponseDTO(User user) {
        UserResponseDto userResponse = new UserResponseDto();

        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setProfileImage(user.getProfileImage());
        userResponse.setIntroduction(user.getIntroduction());
        return userResponse;

    }

    // 회원 탈퇴
    public void deleteUser() {
        CustomUserDetails userDetails = getLoggedInUserDetails();
        String email = userDetails.getUsername();

        // User 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // RefreshToken 삭제
        refreshTokenRepository.deleteByEmail(email);

        // User 삭제
        userRepository.delete(user);
    }


}
