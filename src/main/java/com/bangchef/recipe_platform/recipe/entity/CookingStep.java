package com.bangchef.recipe_platform.recipe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CookingStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber; // 조리 단계 번호

    @Column(name = "description", nullable = false)
    private String description; // 조리 순서 설명

    @Column(name = "image_url")
    private String imageUrl; // 조리 단계 이미지 URL

}