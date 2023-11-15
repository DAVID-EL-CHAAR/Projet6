package com.payMyBuddy.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payMyBuddy.model.Friend;
import com.payMyBuddy.model.User;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUser(User user);
    Friend findByUserAndFriendEmail(User user, String friendEmail);
}

