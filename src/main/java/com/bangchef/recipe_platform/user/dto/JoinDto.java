package com.bangchef.recipe_platform.user.dto;

import com.bangchef.recipe_platform.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JoinDto {

    private String username;

    private String password;

    private String email;

    private Role role; // Role 필드 추가

}
