package com.bangchef.recipe_platform.user.dto;

import com.bangchef.recipe_platform.common.enums.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String username;
    private String email;
    private String password;
    private String profileImage;
    private String introduction;
    private Integer subscribers;
    private Float avgRating;
    private Role role;
}
