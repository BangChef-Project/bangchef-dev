package com.bangchef.recipe_platform.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscriptions")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false) // 구독한 사용자
    private User subscriber;

    @ManyToOne
    @JoinColumn(name = "subscribed_to_id", nullable = false) // 구독받는 사용자
    private User subscribedTo;
}