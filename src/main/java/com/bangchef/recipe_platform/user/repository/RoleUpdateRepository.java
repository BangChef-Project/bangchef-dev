package com.bangchef.recipe_platform.user.repository;

import com.bangchef.recipe_platform.common.enums.RequestStatus;
import com.bangchef.recipe_platform.user.entity.RoleUpdate;
import com.bangchef.recipe_platform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleUpdateRepository extends JpaRepository<RoleUpdate, Long> {
    // PENDING 상태의 등업 요청을 조회 (중복 요청 방지용)
    Optional<RoleUpdate> findByUser_EmailAndStatus(String email, RequestStatus status);
    // 사용자의 요청 상태 조회
    List<RoleUpdate> findByUser_Email(String email);
    // 특정 사용자의 모든 RoleUpdate 삭제
    void deleteByUser(User user);
}
