package com.payMyBuddy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payMyBuddy.model.BankAccount;
import com.payMyBuddy.model.User;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount findByUser(User user);
    Optional<BankAccount> findById(Long id);
    BankAccount findByRib(String rib);
    List<BankAccount> findAllByUser(User user);

}
