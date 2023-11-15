package com.payMyBuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payMyBuddy.model.TransferHistory;

public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
    List<TransferHistory> findByUserId(Long userId);
}
