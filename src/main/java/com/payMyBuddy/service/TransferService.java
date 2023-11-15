package com.payMyBuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

	
	
	public void transferFromBankToPayMyBuddy(User user, String rib, BigDecimal amount) throws Exception {
	    BankAccount bankAccount = bankAccountRepository.findByRib(rib);
	    PayMyBuddyAccount payMyBuddyAccount = payMyBuddyAccountRepository.findByUser(user);

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
	    history.setToAccount("PayMyBuddyAccount"); // Ou un identifiant spécifique
	    history.setAmount(amount);
	    history.setTransferDate(LocalDateTime.now());
	    history.setUser(user);
	    transferHistoryRepository.save(history);
	}

	public void transferFromPayMyBuddyToBank(User user, String rib, BigDecimal amount) throws Exception {
	    BankAccount bankAccount = bankAccountRepository.findByRib(rib);
	    PayMyBuddyAccount payMyBuddyAccount = payMyBuddyAccountRepository.findByUser(user);

	    // Vérifier le solde du compte PayMyBuddy
	    if (payMyBuddyAccount.getBalance().compareTo(amount) < 0) {
	        throw new Exception("Fonds insuffisants sur le compte PayMyBuddy.");
	    }

	    // Effectuer le transfert
	    payMyBuddyAccount.setBalance(payMyBuddyAccount.getBalance().subtract(amount));
	    bankAccount.setBalance(bankAccount.getBalance().add(amount));

	    bankAccountRepository.save(bankAccount);
	    payMyBuddyAccountRepository.save(payMyBuddyAccount);
	    
	    TransferHistory history = new TransferHistory();
	    history.setFromAccount("PayMyBuddyAccount"); // Ou un identifiant spécifique
	    history.setToAccount(rib);
	    history.setAmount(amount);
	    history.setTransferDate(LocalDateTime.now());
	    history.setUser(user);
	    transferHistoryRepository.save(history);
	}
	
	  public void createAndLinkPayMyBuddyAccount(User user) {
	        PayMyBuddyAccount newAccount = new PayMyBuddyAccount();
	        newAccount.setUser(user);
	        newAccount.setBalance(BigDecimal.ZERO); // Commence avec un solde de 0
	        payMyBuddyAccountRepository.save(newAccount);
	    }
	  
	  public List<TransferHistory> getTransferHistory(Long userId) {
		    return transferHistoryRepository.findByUserId(userId);
		}



}
