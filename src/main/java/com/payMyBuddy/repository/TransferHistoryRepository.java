package com.payMyBuddy.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payMyBuddy.model.TransactionHistory;
import com.payMyBuddy.model.TransferHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
    List<TransferHistory> findByUserId(Long userId);
    Page<TransferHistory> findByUserId(Long userId, Pageable pageable);
    
}
