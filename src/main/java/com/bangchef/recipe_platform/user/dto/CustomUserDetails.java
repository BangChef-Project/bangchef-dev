package com.bangchef.recipe_platform.user.dto;

import com.bangchef.recipe_platform.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() ->
                Optional.ofNullable(user.getRole())
                        .map(Object::toString)
                        .orElse("ROLE_USER")
        );
    }

    public long getUserId() {
        return user.getUserId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 이메일 기반 인증
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 확인 가능
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 확인 가능
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 확인 가능
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled(); // 사용자 활성 상태 반환
    }
}
