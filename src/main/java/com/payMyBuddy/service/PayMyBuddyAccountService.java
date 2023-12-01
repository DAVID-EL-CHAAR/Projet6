package com.payMyBuddy.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;

@Service
public class PayMyBuddyAccountService {
	
	@Autowired
	private PayMyBuddyAccountRepository payMyBuddyAccountRepository;
	
	 public void createAndLinkPayMyBuddyAccount(User user) {
	        PayMyBuddyAccount newAccount = new PayMyBuddyAccount();
	        newAccount.setUser(user);
	        newAccount.setBalance(BigDecimal.ZERO); // Commence avec un solde de 0
	        payMyBuddyAccountRepository.save(newAccount);
	    }
	 
	 public PayMyBuddyAccount findByUser(User user) {
	        return payMyBuddyAccountRepository.findByUser(user);
	    }

}
