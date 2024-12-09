package com.bangchef.recipe_platform.user.repository;

import com.bangchef.recipe_platform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(Long userId);

    Optional<User> findByVerificationToken(String token);
}
