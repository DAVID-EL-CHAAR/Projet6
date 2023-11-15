package com.payMyBuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.User;

@Repository
public interface PayMyBuddyAccountRepository extends JpaRepository<PayMyBuddyAccount, Long> {
    
    // Trouver un compte PayMyBuddy par utilisateur
    PayMyBuddyAccount findByUser(User user);
}

