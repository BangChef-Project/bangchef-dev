package com.bangchef.recipe_platform.report.controller;

import com.bangchef.recipe_platform.common.exception.CustomException;
import com.bangchef.recipe_platform.common.exception.ErrorCode;
import com.bangchef.recipe_platform.report.dto.ReportHistoryResponseDto;
import com.bangchef.recipe_platform.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 회원 신고
    @PostMapping("/user")
    public ResponseEntity<String> reportUser(@RequestBody Map<String, String> requestData) {
        String reportedEmail = requestData.get("reportedEmail");
        String reason = requestData.get("reason");

        if (reportedEmail == null || reason == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        reportService.reportUser(reportedEmail, reason);
        return ResponseEntity.ok("회원 신고가 접수되었습니다.");
    }

    // 게시글 신고
    @PostMapping("/recipe")
    public ResponseEntity<String> reportRecipe(@RequestBody Map<String, Object> requestData) {
        Long recipeId = ((Number) requestData.get("recipeId")).longValue();
        String reason = (String) requestData.get("reason");

        if (recipeId == null || reason == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        reportService.reportRecipe(recipeId, reason);
        return ResponseEntity.ok("게시글 신고가 접수되었습니다.");
    }

    // 신고 내역 조회
    @GetMapping("/history")
    public ResponseEntity<List<ReportHistoryResponseDto>> getReportHistory(@RequestParam String reporterEmail) {
        List<ReportHistoryResponseDto> reportHistory = reportService.getReportHistory(reporterEmail);
        return ResponseEntity.ok(reportHistory);
    }
}
