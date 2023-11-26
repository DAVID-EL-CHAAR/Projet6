package com.payMyBuddy.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payMyBuddy.model.TransactionHistory;
import com.payMyBuddy.model.TransferHistory;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
    List<TransferHistory> findByUserId(Long userId);
   
}
