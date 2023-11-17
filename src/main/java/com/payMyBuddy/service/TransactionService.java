package com.payMyBuddy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public void sendMoney(String senderEmail, String recipientEmail, BigDecimal amount, String description) throws Exception {
        User sender = userRepository.findByEmail(senderEmail);
        User recipient = userRepository.findByEmail(recipientEmail);

        if (sender == null || recipient == null) {
            throw new Exception("L'un des utilisateurs n'a pas été trouvé.");
        }
        
        // Vérifier si les deux utilisateurs sont amis
        if (!friendRepository.areFriends(sender, recipient)) {
            throw new Exception("Les transactions ne sont autorisées qu'entre amis.");
        }

        PayMyBuddyAccount senderAccount = payMyBuddyAccountRepository.findByUser(sender);
        PayMyBuddyAccount recipientAccount = payMyBuddyAccountRepository.findByUser(recipient);

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

    /*
    private void recordTransactionHistory(User user, String transactionType, BigDecimal amount, String description) {
        TransactionHistory history = new TransactionHistory();
        history.setUser(user);
        history.setTransactionType(transactionType);
        history.setAmount(amount);
        history.setDate(LocalDateTime.now());
        history.setDescription(description);
        transactionHistoryRepository.save(history);
    }

*/
    
    public List<Transaction> getTransactionsForUser(User user) {
        List<Transaction> sentTransactions = transactionRepository.findAllBySender(user);
        List<Transaction> receivedTransactions = transactionRepository.findAllByRecipient(user);
        
        // Fusionner les listes de transactions envoyées et reçues
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);

        // Trier les transactions par date, si nécessaire
        allTransactions.sort(Comparator.comparing(Transaction::getDate));
// Assurez-vous que Transaction a un champ 'date'

        return allTransactions;
    }

    public LocalDateTime getDate() {
        return date;
    }
    
    public List<Transaction> getSentTransactions(User user) {
        return transactionRepository.findAllBySender(user);
    }

    public List<Transaction> getReceivedTransactions(User user) {
        return transactionRepository.findAllByRecipient(user);
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





    public List<Transaction> getSentTransactions(Long userId) {
        return transactionRepository.findBySenderId(userId);
    }

    public List<Transaction> getReceivedTransactions(Long userId) {
        return transactionRepository.findByRecipientId(userId);
    }

    
}
