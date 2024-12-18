package com.bangchef.recipe_platform.user.controller;

import com.bangchef.recipe_platform.common.enums.RequestStatus;
import com.bangchef.recipe_platform.common.enums.Role;
import com.bangchef.recipe_platform.common.enums.UserSortType;
import com.bangchef.recipe_platform.user.dto.RoleUpdateDto;
import com.bangchef.recipe_platform.user.dto.UserResponseDto;
import com.bangchef.recipe_platform.user.dto.UserUpdateDto;
import com.bangchef.recipe_platform.user.service.RoleUpdateService;
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
    private final RoleUpdateService roleUpdateService;

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

    @GetMapping("/name")
    public ResponseEntity<?> findByUserByName(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortType") UserSortType sortType
    ){
        return ResponseEntity.ok(userService.findUserByName(name, page, sortType));
    }

    // 등업 요청 생성
    @PostMapping("/role-update")
    public ResponseEntity<String> createRoleUpdateRequest(
            @RequestBody RoleUpdateDto requestDto) {
        roleUpdateService.createRoleUpdateRequest(requestDto.getEmail(), requestDto.getRole());
        return ResponseEntity.ok("등업 요청이 완료되었습니다.");
    }

    // 사용자의 등업 요청 상태 조회
    @GetMapping("/role-update/status")
    public ResponseEntity<?> getUserRoleUpdateRequests(@RequestParam String email) {
        String status = roleUpdateService.getUserRoleUpdateStatus(email);
        return ResponseEntity.ok(status);
    }



}
