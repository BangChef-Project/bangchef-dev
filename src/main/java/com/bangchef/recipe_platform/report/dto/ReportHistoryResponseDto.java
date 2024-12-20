package com.bangchef.recipe_platform.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportHistoryResponseDto {
    private String target; // 신고 대상 (회원 이메일 또는 게시글 ID)
    private String type;   // 신고 유형 (회원 신고 / 게시글 신고)
    private String reason; // 신고 사유
    private String status; // 신고 상태 (대기 중 / 처리됨 / 거부됨)
}
