package com.payMyBuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllBySender(User sender);
    List<Transaction> findAllByRecipient(User recipient);
    List<Transaction> findBySenderId(Long senderId);
    List<Transaction> findByRecipientId(Long recipientId);

}

