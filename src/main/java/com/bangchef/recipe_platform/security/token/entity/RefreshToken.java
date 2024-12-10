package com.bangchef.recipe_platform.security.token.entity;

import com.bangchef.recipe_platform.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;


    private String refresh;

    private String expiration; // 만료시간

    private String email;

    private String password;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
