package com.bangchef.recipe_platform.security.token.repository;

import com.bangchef.recipe_platform.security.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefresh(String refresh);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.email = :email")
    void deleteByEmail(@Param("email") String email); // 이메일 기반 삭제 메서드 추가

    boolean existsByEmail(String email);

}
