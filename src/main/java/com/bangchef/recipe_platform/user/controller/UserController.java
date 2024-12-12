package com.bangchef.recipe_platform.user.controller;

import com.bangchef.recipe_platform.user.dto.UserResponseDto;
import com.bangchef.recipe_platform.user.dto.UserUpdateDto;
import com.bangchef.recipe_platform.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getMe() {
        try {
            UserResponseDto userResponse = userService.getLoggedInUser();
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            log.error("사용자 인증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        try {
            UserResponseDto updatedUser = userService.updateUser(userUpdateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            log.error("사용자 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser() {
        try {
            userService.deleteUser();
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (RuntimeException e) {
            log.error("회원 탈퇴 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원 탈퇴 실패: " + e.getMessage());
        }
    }
}
