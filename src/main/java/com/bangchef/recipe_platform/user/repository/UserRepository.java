package com.bangchef.recipe_platform.user.repository;

import com.bangchef.recipe_platform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT user FROM User user WHERE user.username LIKE %:userName%")
    List<User> findByUserNameLike(@Param("userName") String userName);

    Optional<User> findByVerificationToken(String token);
    Optional<User> findByEmail(String email);
}
