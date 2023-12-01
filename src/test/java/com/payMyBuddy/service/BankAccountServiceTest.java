package com.payMyBuddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.payMyBuddy.model.BankAccount;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.BankAccountRepository;
import com.payMyBuddy.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class BankAccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addBankAccountToUser_ShouldThrowException_WhenRibExists() {
        String rib = "123456";
        when(bankAccountRepository.findByRib(rib)).thenReturn(new BankAccount());

        assertThrows(Exception.class, () -> {
            bankAccountService.addBankAccountToUser("user@example.com", rib, BigDecimal.valueOf(20), "Nom", "Prenom");
        });
    }

    @Test
    public void addBankAccountToUser_ShouldThrowException_WhenInitialBalanceIsLow() {
        BigDecimal lowBalance = BigDecimal.valueOf(5);

        assertThrows(Exception.class, () -> {
            bankAccountService.addBankAccountToUser("user@example.com", "123456", lowBalance, "Nom", "Prenom");
        });
    }

    @Test
    public void addBankAccountToUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            bankAccountService.addBankAccountToUser("user@example.com", "123456", BigDecimal.valueOf(20), "Nom", "Prenom");
        });
    }

    @Test
    public void addBankAccountToUser_ShouldCreateAccount_WhenValidRequest() throws Exception {
        String userEmail = "user@example.com";
        String rib = "123456";
        BigDecimal initialBalance = BigDecimal.valueOf(20);
        String nom = "Nom";
        String prenom = "Prenom";

        User user = new User();
        user.setEmail(userEmail);

        when(bankAccountRepository.findByRib(rib)).thenReturn(null);
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
       // when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(new BankAccount());

        bankAccountService.addBankAccountToUser(userEmail, rib, initialBalance, nom, prenom);

        ArgumentCaptor<BankAccount> bankAccountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(bankAccountCaptor.capture());

        BankAccount savedBankAccount = bankAccountCaptor.getValue();

        assertNotNull(savedBankAccount);
        assertEquals(rib, savedBankAccount.getRib());
        assertEquals(initialBalance, savedBankAccount.getBalance());
        assertEquals(nom, savedBankAccount.getNom());
        assertEquals(prenom, savedBankAccount.getPrenom());
        assertEquals(user, savedBankAccount.getUser());
    }

    @Test
    public void updateBankAccount_ShouldThrowException_WhenRibExistsAndDifferentId() {
        // Configuration initiale
        Long bankAccountId = 1L;
        String rib = "123456";
        BigDecimal newBalance = BigDecimal.valueOf(20);
        String nom = "Nom";
        String prenom = "Prenom";

        BankAccount otherAccount = new BankAccount();
        otherAccount.setId(2L); // Un ID différent

        when(bankAccountRepository.findByRib(rib)).thenReturn(otherAccount);

        // Test
        assertThrows(Exception.class, () -> {
            bankAccountService.updateBankAccount(bankAccountId, rib, newBalance, nom, prenom);
        });
    }

    @Test
    public void updateBankAccount_ShouldUpdate_WhenValidRequest() throws Exception {
        // Configuration initiale
        Long bankAccountId = 1L;
        String rib = "123456";
        BigDecimal newBalance = BigDecimal.valueOf(20);
        String nom = "Nom";
        String prenom = "Prenom";

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(bankAccountId);

        when(bankAccountRepository.findByRib(rib)).thenReturn(bankAccount);
        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(bankAccount));

        // Test
        bankAccountService.updateBankAccount(bankAccountId, rib, newBalance, nom, prenom);

        verify(bankAccountRepository).save(any(BankAccount.class));
    }

  
}
