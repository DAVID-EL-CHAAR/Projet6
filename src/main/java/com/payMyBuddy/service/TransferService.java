package com.payMyBuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import com.payMyBuddy.model.BankAccount;
import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.TransferHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.BankAccountRepository;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;
import com.payMyBuddy.repository.TransferHistoryRepository;

@Service
public class TransferService {
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@Autowired
	private PayMyBuddyAccountRepository payMyBuddyAccountRepository;
	
	@Autowired
	private TransferHistoryRepository transferHistoryRepository;

	
	@Transactional
	public void transferFromBankToPayMyBuddy(User user, String rib, BigDecimal amount) throws Exception {
	    BankAccount bankAccount = bankAccountRepository.findByRib(rib);
	    
	    if (amount.compareTo(BigDecimal.ONE) < 0) {
	        throw new Exception("Le montant doit être supérieur ou égal à 1.");
	    }
	    
	    if (bankAccount == null) {
	        throw new Exception("Compte bancaire inexistant");
	    }
	    
	    if (!bankAccount.getUser().equals(user)) {
            throw new Exception("Opération non autorisée car ce compte ne vous apparitient pas ou vous n'avez pas encore creer un compte bancaire");
            
        }
	    PayMyBuddyAccount payMyBuddyAccount = payMyBuddyAccountRepository.findByUser(user);

	    if (payMyBuddyAccount == null) {
	        throw new Exception("Veuillez activer votre Compte PayMyBuddy ");
	    }
	    
	    // Vérifier le solde du compte bancaire
	    if (bankAccount.getBalance().compareTo(amount) < 0) {
	    	throw new Exception("Fonds insuffisants sur le compte bancaire.");
	    }

	    // Effectuer le transfert
	    bankAccount.setBalance(bankAccount.getBalance().subtract(amount));
	    payMyBuddyAccount.setBalance(payMyBuddyAccount.getBalance().add(amount));

	    bankAccountRepository.save(bankAccount);
	    payMyBuddyAccountRepository.save(payMyBuddyAccount);
	    
	    TransferHistory history = new TransferHistory();
	    history.setFromAccount(rib);
	    history.setToAccount("PayMyBuddyAccount"); 
	    history.setAmount(amount);
	    history.setTransferDate(LocalDateTime.now());
	    history.setUser(user);
	    transferHistoryRepository.save(history);
	}

	@Transactional
	public void transferFromPayMyBuddyToBank(User user, String rib, BigDecimal amount) throws Exception {
	    if (amount.compareTo(BigDecimal.ONE) < 0) {
	        throw new Exception("Le montant doit être supérieur ou égal à 1.");
	    }

	    BankAccount bankAccount = bankAccountRepository.findByRib(rib);
	    if (bankAccount == null || !bankAccount.getUser().equals(user)) {
	        throw new Exception("Opération non autorisée ou compte bancaire inexistant.");
	    }

	    PayMyBuddyAccount payMyBuddyAccount = payMyBuddyAccountRepository.findByUser(user);
	    
	    if (payMyBuddyAccount == null) {
	        throw new Exception("Veuillez activer votre Compte PayMyBuddy ");
	    }
	    
	    if (payMyBuddyAccount.getBalance().compareTo(amount) < 0) {
	        throw new Exception("Fonds insuffisants sur le compte PayMyBuddy.");
	    }
	    

	    // Effectuer le transfert
	    payMyBuddyAccount.setBalance(payMyBuddyAccount.getBalance().subtract(amount));
	    bankAccount.setBalance(bankAccount.getBalance().add(amount));

	    bankAccountRepository.save(bankAccount);
	    payMyBuddyAccountRepository.save(payMyBuddyAccount);
	    
	    TransferHistory history = new TransferHistory();
	    history.setFromAccount("PayMyBuddyAccount"); 
	    history.setToAccount(rib);
	    history.setAmount(amount);
	    history.setTransferDate(LocalDateTime.now());
	    history.setUser(user);
	    transferHistoryRepository.save(history);
	}
	
	  
	  
	  @Transactional(readOnly = true)
	  public List<TransferHistory> getTransferHistory(Long userId) {
		    return transferHistoryRepository.findByUserId(userId);
		}
	
	/*  @Transactional(readOnly = true)
	  public Page<TransferHistory> getTransferHistory(Long userId, Pageable pageable) {
	      return transferHistoryRepository.findByUserId(userId, pageable);
	  }*/




}
