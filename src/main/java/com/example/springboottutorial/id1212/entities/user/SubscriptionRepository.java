package com.example.springboottutorial.id1212.entities.user;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
    Subscription findSubscriptionByChatroomId(Integer chatroomId);
    ArrayList<Subscription> findSubscriptionsByUserId(Integer userId);
}