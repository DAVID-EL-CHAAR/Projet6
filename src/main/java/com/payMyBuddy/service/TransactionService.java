package com.payMyBuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.TransactionHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.FriendRepository;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;
import com.payMyBuddy.repository.TransactionHistoryRepository;
import com.payMyBuddy.repository.TransactionRepository;
import com.payMyBuddy.repository.UserRepository;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayMyBuddyAccountRepository payMyBuddyAccountRepository;
    
    @Autowired
    private FriendRepository friendRepository;
    
    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;
    
    private LocalDateTime date;

    @Transactional
    public void sendMoney(String senderEmail, String recipientEmail, BigDecimal amount, String description) throws Exception {
    	
        if (amount.compareTo(BigDecimal.ONE) < 0) {
    	    throw new Exception("Le montant doit être supérieur ou égal à 1.");
    	    }
    	  
        User sender = userRepository.findByEmail(senderEmail);
        User recipient = userRepository.findByEmail(recipientEmail);

        if (sender == null || recipient == null) {
            throw new Exception(" utilisateurs n'a pas été trouvé.");
        }
        
        // Vérifier si les deux utilisateurs sont amis
        if (!friendRepository.areFriends(sender, recipient)) {
            throw new Exception("Les transactions ne sont autorisées qu'entre amis.");
        }

        PayMyBuddyAccount senderAccount = payMyBuddyAccountRepository.findByUser(sender);
        PayMyBuddyAccount recipientAccount = payMyBuddyAccountRepository.findByUser(recipient);
        
        // Vérifier si les comptes PayMyBuddy existent
        if (senderAccount == null) {
            throw new Exception("votre compte payMyBuddy n'est pas encore activer veuillez l'activer en vous rendant dans la page profile.");
        }
        if (recipientAccount == null) {
            throw new Exception("Le compte PayMyBuddy du destinataire est inexistant ou pas encore activer .");
        }

        BigDecimal commission = amount.multiply(BigDecimal.valueOf(0.005)); // 0.5% commission
        if (senderAccount.getBalance().compareTo(amount.add(commission)) < 0) {
            throw new Exception("Solde insuffisant pour effectuer la transaction.");
        }

        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setRecipient(recipient);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDate(LocalDateTime.now()); // Définir la date actuelle pour la transaction

        transactionRepository.save(transaction);

        // Mise à jour des comptes PayMyBuddy
        senderAccount.adjustBalance(amount.add(commission), false); // Débit du montant + commission
        recipientAccount.adjustBalance(amount, true); // Crédit du montant

        payMyBuddyAccountRepository.save(senderAccount);
        payMyBuddyAccountRepository.save(recipientAccount);

        // Enregistrer dans l'historique des transactions
        recordTransactionHistory(sender, recipient, amount, description);
    }

    
    
    private void recordTransactionHistory(User sender, User recipient, BigDecimal amount, String description) {
        TransactionHistory history = new TransactionHistory();
        history.setSender(sender);
        history.setRecipient(recipient);
        history.setAmount(amount);
        history.setDate(LocalDateTime.now());
        history.setDescription(description);
        transactionHistoryRepository.save(history);
    }



    public List<TransactionHistory> findBySender(User sender) {
        return transactionHistoryRepository.findBySender(sender);
    }

    public List<TransactionHistory> findByRecipient(User recipient) {
        return transactionHistoryRepository.findByRecipient(recipient);
    }
}
