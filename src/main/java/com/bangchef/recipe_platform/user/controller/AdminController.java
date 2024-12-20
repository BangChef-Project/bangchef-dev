package com.bangchef.recipe_platform.user.controller;

import com.bangchef.recipe_platform.common.enums.RequestStatus;
import com.bangchef.recipe_platform.common.exception.CustomException;
import com.bangchef.recipe_platform.common.exception.ErrorCode;
import com.bangchef.recipe_platform.recipe.service.RecipeService;
import com.bangchef.recipe_platform.report.dto.ReportHistoryResponseDto;
import com.bangchef.recipe_platform.report.service.ReportService;
import com.bangchef.recipe_platform.user.dto.UserResponseDto;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import com.bangchef.recipe_platform.user.service.AdminService;
import com.bangchef.recipe_platform.user.service.RoleUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final RoleUpdateService roleUpdateService;
    private final UserRepository userRepository;
    private final RecipeService recipeService;
    private final ReportService reportService;

    public AdminController(AdminService adminService, RoleUpdateService roleUpdateService, UserRepository userRepository, RecipeService recipeService, ReportService reportService) {
        this.adminService = adminService;
        this.roleUpdateService = roleUpdateService;
        this.userRepository = userRepository;
        this.recipeService = recipeService;
        this.reportService = reportService;
    }


    // 사용자 권한 변경 (Admin 전용, email 기반)
    @PutMapping("/users/{email}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(
            @PathVariable("email") String email,
            @RequestParam("role") String role) {
        UserResponseDto updatedUser = adminService.updateUserRole(email, role);
        return ResponseEntity.ok(updatedUser);
    }

    // 관리자: 등업 요청 상태 업데이트
    @PutMapping("/role-update/{requestId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateRoleUpdateStatus(
            @PathVariable Long requestId, @RequestParam RequestStatus status) {
        roleUpdateService.updateRoleUpdateStatus(requestId, status);
        return ResponseEntity.ok("등업 요청을 처리했습니다.");
    }


    @DeleteMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@RequestParam("email") String email) {
        adminService.deleteUser(email);
        return new ResponseEntity<>("해당 회원의 탈퇴 처리가 완료되었습니다.", HttpStatus.OK);
    }

    // 관리자 : 특정 레시피 삭제
    @DeleteMapping("/recipes/{recipeId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipeById(recipeId);
        return ResponseEntity.ok("레시피가 삭제되었습니다.");
    }

    // 관리자 : 특정 회원 레시피 일괄 삭제
    @DeleteMapping("/recipes/all/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteRecipesByUser(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        recipeService.deleteRecipesByUser(user);
        return ResponseEntity.ok("사용자의 모든 레시피가 삭제되었습니다.");
    }

    // 신고 내역 조회
    @GetMapping("/reports/history")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReportHistoryResponseDto>> getPendingReports() {
        List<ReportHistoryResponseDto> pendingReports = reportService.getPendingReports();
        return ResponseEntity.ok(pendingReports);
    }

}
