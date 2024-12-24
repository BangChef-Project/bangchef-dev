package com.bangchef.recipe_platform.user.repository;

import com.bangchef.recipe_platform.user.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    @Query("SELECT sc FROM Subscription sc WHERE sc.subscriber.email = :subscriberEmail")
    List<Subscription> findByUserEmail(@Param("subscriberEmail") String subscriberEmail);
}
