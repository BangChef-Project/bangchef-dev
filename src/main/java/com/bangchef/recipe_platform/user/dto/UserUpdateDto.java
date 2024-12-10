package com.bangchef.recipe_platform.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;


    private String profileImage;

    @Size(max = 200, message = "소개글은 200자 이내로 작성해주세요.")
    private String introduction;
}
