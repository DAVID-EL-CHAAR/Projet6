package com.payMyBuddy.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.payMyBuddy.model.BankAccount;
import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.TransactionHistory;
import com.payMyBuddy.model.TransferHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.BankAccountRepository;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;
import com.payMyBuddy.repository.TransferHistoryRepository;

public class TransferServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private PayMyBuddyAccountRepository payMyBuddyAccountRepository;

    @Mock
    private TransferHistoryRepository transferHistoryRepository;

    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }	
	@Test
	public void transferFromBankToPayMyBuddy_ShouldThrowException_WhenAmountIsLessThanOne() {
	   
	    User user = new User();
	    String rib = "123456";
	    BigDecimal amount = BigDecimal.ZERO;

	    // Test
	    assertThrows(Exception.class, () -> {
	        transferService.transferFromBankToPayMyBuddy(user, rib, amount);
	    });
	}

	@Test
	public void transferFromBankToPayMyBuddy_ShouldPerformTransfer_WhenValidRequest() throws Exception {
	    
	    User user = new User();
	    String rib = "123456";
	    BigDecimal amount = BigDecimal.TEN;
	    BankAccount bankAccount = new BankAccount();
	    bankAccount.setBalance(BigDecimal.valueOf(20));
	    bankAccount.setUser(user);

	    PayMyBuddyAccount payMyBuddyAccount = new PayMyBuddyAccount();
	    payMyBuddyAccount.setBalance(BigDecimal.ZERO);

	    when(bankAccountRepository.findByRib(rib)).thenReturn(bankAccount);
	    when(payMyBuddyAccountRepository.findByUser(user)).thenReturn(payMyBuddyAccount);

	    // Test
	    transferService.transferFromBankToPayMyBuddy(user, rib, amount);

	    verify(bankAccountRepository).save(any(BankAccount.class));
	    verify(payMyBuddyAccountRepository).save(any(PayMyBuddyAccount.class));
	    verify(transferHistoryRepository).save(any(TransferHistory.class));
	}

	@Test
	public void transferFromPayMyBuddyToBank_ShouldThrowException_WhenAmountIsLessThanOne() {
	    
	    User user = new User();
	    String rib = "123456";
	    BigDecimal amount = BigDecimal.ZERO;

	    // Test
	    assertThrows(Exception.class, () -> {
	        transferService.transferFromPayMyBuddyToBank(user, rib, amount);
	    });
	}

	@Test
	public void transferFromPayMyBuddyToBank_ShouldPerformTransfer_WhenValidRequest() throws Exception {
	    
	    User user = new User();
	    String rib = "123456";
	    BigDecimal amount = BigDecimal.TEN;
	    BankAccount bankAccount = new BankAccount();
	    bankAccount.setBalance(BigDecimal.valueOf(20));
	    bankAccount.setUser(user);

	    PayMyBuddyAccount payMyBuddyAccount = new PayMyBuddyAccount();
	    payMyBuddyAccount.setBalance(BigDecimal.valueOf(20));
	    payMyBuddyAccount.setUser(user);

	    when(bankAccountRepository.findByRib(rib)).thenReturn(bankAccount);
	    when(payMyBuddyAccountRepository.findByUser(user)).thenReturn(payMyBuddyAccount);

	    
	    // Test
	    transferService.transferFromPayMyBuddyToBank(user, rib, amount);
	    

        ArgumentCaptor<TransferHistory> historyCaptor = ArgumentCaptor.forClass(TransferHistory.class);
        verify(transferHistoryRepository).save(historyCaptor.capture());
        TransferHistory capturedHistory = historyCaptor.getValue();

        
        assertNotNull(capturedHistory);


        assertNotNull(capturedHistory);
        assertEquals(user, capturedHistory.getUser());
	    verify(bankAccountRepository).save(any(BankAccount.class));
	    verify(payMyBuddyAccountRepository).save(any(PayMyBuddyAccount.class));
	    
	}
	
	  @Test
	    public void getTransferHistory_ShouldReturnTransferHistories_WhenUserIdIsGiven() {
	        Long userId = 1L;
	        TransferHistory history1 = new TransferHistory(); 
	        TransferHistory history2 = new TransferHistory(); 

	        when(transferHistoryRepository.findByUserId(userId)).thenReturn(Arrays.asList(history1, history2));

	        List<TransferHistory> result = transferService.getTransferHistory(userId);

	        assertNotNull(result);
	        assertEquals(2, result.size());
	        assertTrue(result.containsAll(Arrays.asList(history1, history2)));
	    }


}
