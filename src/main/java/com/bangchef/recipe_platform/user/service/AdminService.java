package com.bangchef.recipe_platform.user.service;

import com.bangchef.recipe_platform.common.enums.Role;
import com.bangchef.recipe_platform.user.dto.UserResponseDto;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 사용자 권한 변경 (ADMIN 전용)
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponseDto updateUserRole(String email, String role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Role 변경 로직
        user.setRole(role.equalsIgnoreCase("ADMIN") ? Role.ADMIN : Role.USER);
        userRepository.save(user);

        // 변경된 정보를 UserResponseDto로 반환
        return mapToUserResponseDto(user);
    }

    // 사용자 삭제 (ADMIN 전용)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        userRepository.delete(user);
    }

    // User 엔티티를 UserResponseDto로 변환
    private UserResponseDto mapToUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword()); // 보안상 암호화된 값 제공
        dto.setProfileImage(user.getProfileImage());
        dto.setIntroduction(user.getIntroduction());
        dto.setSubscribers(user.getSubscribers());
        dto.setAvgRating(user.getAvgRating());
        dto.setRole(user.getRole());
        return dto;
    }

}
