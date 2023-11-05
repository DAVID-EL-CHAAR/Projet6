package com.payMyBuddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.payMyBuddy.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
	
	User findByEmail(String email);
	
}