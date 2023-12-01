package com.payMyBuddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.TransactionHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.FriendRepository;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;
import com.payMyBuddy.repository.TransactionHistoryRepository;
import com.payMyBuddy.repository.TransactionRepository;
import com.payMyBuddy.repository.UserRepository;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PayMyBuddyAccountRepository payMyBuddyAccountRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendMoney_ShouldThrowException_WhenAmountIsLessThanOne() {
        
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";
        BigDecimal amount = BigDecimal.ZERO;

        // Test
        assertThrows(Exception.class, () -> {
            transactionService.sendMoney(senderEmail, recipientEmail, amount, "Test");
        });
    }

    @Test
    public void sendMoney_ShouldThrowException_WhenUsersNotFound() {
       
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";
        BigDecimal amount = BigDecimal.TEN;

        when(userRepository.findByEmail(senderEmail)).thenReturn(null);

        // Test
        assertThrows(Exception.class, () -> {
            transactionService.sendMoney(senderEmail, recipientEmail, amount, "Test");
        });
    }

    @Test
    public void sendMoney_ShouldThrowException_WhenUsersAreNotFriends() {
        // Configuration initiale
        User sender = new User();
        User recipient = new User();
        
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";
        BigDecimal amount = BigDecimal.TEN;

        when(userRepository.findByEmail(senderEmail)).thenReturn(sender);
        when(userRepository.findByEmail(recipientEmail)).thenReturn(recipient);
        when(friendRepository.areFriends(sender, recipient)).thenReturn(false);

        // Test
        assertThrows(Exception.class, () -> {
            transactionService.sendMoney(senderEmail, recipientEmail, amount, "Test");
        });
    }

    @Test
    public void sendMoney_ShouldThrowException_WhenSenderBalanceIsInsufficient() {
        
        User sender = new User();
        User recipient = new User();
        PayMyBuddyAccount senderAccount = new PayMyBuddyAccount();
        senderAccount.setBalance(BigDecimal.valueOf(5)); // Solde insuffisant

        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";
        BigDecimal amount = BigDecimal.TEN;

        when(userRepository.findByEmail(senderEmail)).thenReturn(sender);
        when(userRepository.findByEmail(recipientEmail)).thenReturn(recipient);
        when(friendRepository.areFriends(sender, recipient)).thenReturn(true);
        when(payMyBuddyAccountRepository.findByUser(sender)).thenReturn(senderAccount);

        // Test
        assertThrows(Exception.class, () -> {
            transactionService.sendMoney(senderEmail, recipientEmail, amount, "Test");
        });
    }

    @Test
    public void sendMoney_ShouldPerformTransaction_WhenValidRequest() throws Exception {
      
        User sender = new User();
        User recipient = new User();
        PayMyBuddyAccount senderAccount = new PayMyBuddyAccount();
        senderAccount.setBalance(BigDecimal.valueOf(100));

        PayMyBuddyAccount recipientAccount = new PayMyBuddyAccount();
        recipientAccount.setBalance(BigDecimal.ZERO);

        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";
        BigDecimal amount = BigDecimal.TEN;

        when(userRepository.findByEmail(senderEmail)).thenReturn(sender);
        when(userRepository.findByEmail(recipientEmail)).thenReturn(recipient);
        when(friendRepository.areFriends(sender, recipient)).thenReturn(true);
        when(payMyBuddyAccountRepository.findByUser(sender)).thenReturn(senderAccount);
        when(payMyBuddyAccountRepository.findByUser(recipient)).thenReturn(recipientAccount);
        
        ArgumentCaptor<Transaction> userCaptor = ArgumentCaptor.forClass(Transaction.class);
        // Test
        transactionService.sendMoney(senderEmail, recipientEmail, amount, "Test");

        verify(transactionRepository).save(userCaptor.capture());
        verify(payMyBuddyAccountRepository, times(2)).save(any(PayMyBuddyAccount.class));
        verify(transactionHistoryRepository).save(any());
        
        Transaction capturedUser = userCaptor.getValue();
        assertEquals(amount, capturedUser.getAmount());
        
    }
    
    @Test
    public void sendMoney_ShouldRecordTransactionHistory_WhenValidRequest() throws Exception {
        String senderEmail = "sender@example.com";
        String recipientEmail = "recipient@example.com";
        BigDecimal amount = BigDecimal.TEN;
        String description = "Test transfer";

        User sender = new User();
        sender.setEmail(senderEmail);

        User recipient = new User();
        recipient.setEmail(recipientEmail);

        PayMyBuddyAccount senderAccount = new PayMyBuddyAccount();
        senderAccount.setBalance(BigDecimal.valueOf(100));
        senderAccount.setUser(sender);

        PayMyBuddyAccount recipientAccount = new PayMyBuddyAccount();
        recipientAccount.setBalance(BigDecimal.ZERO);
        recipientAccount.setUser(recipient);

        when(userRepository.findByEmail(senderEmail)).thenReturn(sender);
        when(userRepository.findByEmail(recipientEmail)).thenReturn(recipient);
        when(friendRepository.areFriends(sender, recipient)).thenReturn(true);
        when(payMyBuddyAccountRepository.findByUser(sender)).thenReturn(senderAccount);
        when(payMyBuddyAccountRepository.findByUser(recipient)).thenReturn(recipientAccount);

        transactionService.sendMoney(senderEmail, recipientEmail, amount, description);

        ArgumentCaptor<TransactionHistory> historyCaptor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(transactionHistoryRepository).save(historyCaptor.capture());
        TransactionHistory capturedHistory = historyCaptor.getValue();

        assertNotNull(capturedHistory);
        assertEquals(sender, capturedHistory.getSender());
        assertEquals(recipient, capturedHistory.getRecipient());
        assertEquals(amount, capturedHistory.getAmount());
        assertEquals(description, capturedHistory.getDescription());
        assertNotNull(capturedHistory.getDate());
    }
    

    @Test
    public void testFindBySender() {
        // Given
        User sender = new User();
        TransactionHistory transaction = new TransactionHistory();
        transaction.setSender(sender);
        List<TransactionHistory> expectedTransactions = Collections.singletonList(transaction);
        when(transactionHistoryRepository.findBySender(sender)).thenReturn(expectedTransactions);

        // When
        List<TransactionHistory> resultTransactions = transactionService.findBySender(sender);

        // Then
        assertEquals(expectedTransactions, resultTransactions);
        verify(transactionHistoryRepository).findBySender(sender);
    }

    @Test
    public void testFindByRecipient() {
        // Given
        User recipient = new User();
        TransactionHistory transaction = new TransactionHistory();
        transaction.setRecipient(recipient);
        List<TransactionHistory> expectedTransactions = Collections.singletonList(transaction);
        when(transactionHistoryRepository.findByRecipient(recipient)).thenReturn(expectedTransactions);

        // When
        List<TransactionHistory> resultTransactions = transactionService.findByRecipient(recipient);

        // Then
        assertEquals(expectedTransactions, resultTransactions);
        verify(transactionHistoryRepository).findByRecipient(recipient);
    }


}

