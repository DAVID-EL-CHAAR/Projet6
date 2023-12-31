package com.payMyBuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payMyBuddy.model.TransactionHistory;
import com.payMyBuddy.model.User;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
	 
    List<TransactionHistory> findBySender(User sender);
    List<TransactionHistory> findByRecipient(User recipient);

}


