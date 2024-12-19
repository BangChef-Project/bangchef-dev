package com.bangchef.recipe_platform.report.service;

import com.bangchef.recipe_platform.common.enums.ReportStatus;
import com.bangchef.recipe_platform.report.dto.ReportHistoryResponseDto;
import com.bangchef.recipe_platform.report.entity.Report;
import com.bangchef.recipe_platform.report.repository.ReportRepository;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import com.bangchef.recipe_platform.recipe.repository.RecipeRepository;
import com.bangchef.recipe_platform.common.exception.CustomException;
import com.bangchef.recipe_platform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    // 회원 신고
    public void reportUser(String reportedEmail, String reason) {
        User reporter = getLoggedInUser();
        User reportedUser = userRepository.findByEmail(reportedEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_USER_NOT_FOUND));

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(reason)
                .status(ReportStatus.PENDING)
                .build();

        reportRepository.save(report);
    }

    // 게시글 신고
    public void reportRecipe(Long recipeId, String reason) {
        User reporter = getLoggedInUser();
        Recipe reportedRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_RECIPE_NOT_FOUND));

        Report report = Report.builder()
                .reporter(reporter)
                .reportedRecipe(reportedRecipe)
                .reason(reason)
                .status(ReportStatus.PENDING)
                .build();

        reportRepository.save(report);
    }

    // 사용자의 신고 내역 조회 (한국어 상태 메시지 변환 포함)
    public List<ReportHistoryResponseDto> getReportHistory(String reporterEmail) {
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Report> reports = reportRepository.findAllByReporter(reporter);

        return reports.stream()
                .map(report -> new ReportHistoryResponseDto(
                        report.getReportedUser() != null ? report.getReportedUser().getEmail() : "게시글 ID: " + report.getReportedRecipe().getId(),
                        report.getReportedUser() != null ? "회원 신고" : "게시글 신고",
                        report.getReason(),
                        convertStatusToKorean(report.getStatus())
                ))
                .collect(Collectors.toList());
    }

    private String convertStatusToKorean(ReportStatus status) {
        switch (status) {
            case PENDING:
                return "대기 중";
            case RESOLVED:
                return "처리됨";
            case REJECTED:
                return "거부됨";
            default:
                return "알 수 없음";
        }
    }

    // 관리자의 대기중인 모든 신고 내역 조회
    public List<ReportHistoryResponseDto> getPendingReports() {
        List<Report> reports = reportRepository.findAllByStatus(ReportStatus.PENDING);

        return reports.stream()
                .map(report -> new ReportHistoryResponseDto(
                        report.getReportedUser() != null ? report.getReportedUser().getEmail() : "게시글 ID: " + report.getReportedRecipe().getId(),
                        report.getReportedUser() != null ? "회원 신고" : "게시글 신고",
                        report.getReason(),
                        null // 상태는 필요하지 않으므로 null로 설정
                ))
                .collect(Collectors.toList());
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
