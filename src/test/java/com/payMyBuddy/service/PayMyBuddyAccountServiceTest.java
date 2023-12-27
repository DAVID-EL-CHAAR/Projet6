package com.payMyBuddy.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;

import java.math.BigDecimal;

public class PayMyBuddyAccountServiceTest {

    @Mock
    private PayMyBuddyAccountRepository payMyBuddyAccountRepository;

    @InjectMocks
    private PayMyBuddyAccountService payMyBuddyAccountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createAndLinkPayMyBuddyAccount_ShouldCreateAccount_WhenCalledWithUser() {
        User user = new User();
        

        payMyBuddyAccountService.createAndLinkPayMyBuddyAccount(user);

        verify(payMyBuddyAccountRepository, times(1)).save(any(PayMyBuddyAccount.class));
    }

    @Test
    public void createAndLinkPayMyBuddyAccount_ShouldSetInitialBalanceToZero() {
        User user = new User();
        

        doAnswer(invocation -> {
            PayMyBuddyAccount account = invocation.getArgument(0);
            assertEquals(BigDecimal.ZERO, account.getBalance());
            return null;
        }).when(payMyBuddyAccountRepository).save(any(PayMyBuddyAccount.class));

        payMyBuddyAccountService.createAndLinkPayMyBuddyAccount(user);
    }
  

    @Test
    public void testFindByUser() {
        // Given
        User user = new User();
        PayMyBuddyAccount expectedAccount = new PayMyBuddyAccount();
        expectedAccount.setUser(user);
        when(payMyBuddyAccountRepository.findByUser(user)).thenReturn(expectedAccount);

        // When
        PayMyBuddyAccount resultAccount = payMyBuddyAccountService.findByUser(user);

        // Then
        assertEquals(expectedAccount, resultAccount);
        verify(payMyBuddyAccountRepository).findByUser(user);
    }
}

