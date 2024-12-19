package com.bangchef.recipe_platform.user.dto;

import com.bangchef.recipe_platform.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateDto {

    @NotNull(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotNull(message = "요청된 Role은 필수입니다.")
    private Role role;
}
