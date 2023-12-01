package com.payMyBuddy.service;

import static org.junit.jupiter.api.Assertions.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BankAccountServiceTest2 {

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
    public void findAll_ShouldReturnAllAccounts() {
        when(bankAccountRepository.findAll()).thenReturn(Arrays.asList(new BankAccount(), new BankAccount()));

        List<BankAccount> result = bankAccountService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void findAllByUser_ShouldReturnUserAccounts() {
        User user = new User();
        when(bankAccountRepository.findAllByUser(user)).thenReturn(Arrays.asList(new BankAccount(), new BankAccount()));

        List<BankAccount> result = bankAccountService.findAllByUser(user);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void findById_ShouldReturnBankAccount_WhenFound() {
        Long id = 1L;
        BankAccount bankAccount = new BankAccount();
        when(bankAccountRepository.findById(id)).thenReturn(Optional.of(bankAccount));

        BankAccount result = bankAccountService.findById(id);

        assertNotNull(result);
    }

    @Test
    public void findById_ShouldReturnNull_WhenNotFound() {
        Long id = 1L;
        when(bankAccountRepository.findById(id)).thenReturn(Optional.empty());

        BankAccount result = bankAccountService.findById(id);

        assertNull(result);
    }

    @Test
    public void save_ShouldSaveBankAccount() {
        BankAccount bankAccount = new BankAccount();

        bankAccountService.save(bankAccount);

        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    public void delete_ShouldDeleteBankAccount() {
        Long id = 1L;

        bankAccountService.delete(id);

        verify(bankAccountRepository).deleteById(id);
    }

}