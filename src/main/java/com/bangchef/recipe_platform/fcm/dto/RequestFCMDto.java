package com.bangchef.recipe_platform.fcm.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestFCMDto {
    private String title;
    private String body;
}
