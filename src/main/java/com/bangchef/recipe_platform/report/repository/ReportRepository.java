package com.bangchef.recipe_platform.report.repository;

import com.bangchef.recipe_platform.common.enums.ReportStatus;
import com.bangchef.recipe_platform.report.entity.Report;
import com.bangchef.recipe_platform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 신고자를 기준으로 신고 내역 조회
    List<Report> findAllByReporter(User reporter);
    // 대기중 상태의 모든 신고 조회
    List<Report> findAllByStatus(ReportStatus status);
}
