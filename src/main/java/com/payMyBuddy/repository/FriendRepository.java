package com.payMyBuddy.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payMyBuddy.model.Friend;
import com.payMyBuddy.model.User;



@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUser(User user);
    Friend findByUserAndFriendEmail(User user, String friendEmail);
 // Méthode pour vérifier si deux utilisateurs sont amis
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f WHERE (f.user = :user1 AND f.friend = :user2) OR (f.user = :user2 AND f.friend = :user1)")
    boolean areFriends(@Param("user1") User user1, @Param("user2") User user2);
}

