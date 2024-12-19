package com.bangchef.recipe_platform.user.entity;

import com.bangchef.recipe_platform.common.enums.RequestStatus;
import com.bangchef.recipe_platform.common.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RoleUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Role requestedRole; // 요청된 등급 (예: CHEF)

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // 요청 상태 (PENDING, APPROVED, REJECTED)
}
