package com.bangchef.recipe_platform.user.dto;

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
}
