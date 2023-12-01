package com.payMyBuddy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.payMyBuddy.model.TransactionHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.TransactionHistoryRepository;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.TransactionService;
import com.payMyBuddy.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService mockTransactionService;

    @Mock
    private UserRepository mockUserRepository;
    
    @Mock
    private UserService mockUserService;

    @Mock
    private TransactionHistoryRepository mockTransactionHistoryRepository;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                                 .setViewResolvers(viewResolver())
                                 .build();
    }

    private InternalResourceViewResolver viewResolver() {
    	InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    	viewResolver.setPrefix("classpath:/templates/");
    	viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Test
    public void testShowSendMoneyForm() throws Exception {
        mockMvc.perform(get("/transactions/sendMoney"))
            .andExpect(status().isOk())
            .andExpect(view().name("sendMoney"));
    }

    @Test
    public void testSendMoney_Success() throws Exception {
        String userEmail = "user@example.com";

        mockMvc.perform(post("/transactions/sendMoney")
            .principal(() -> userEmail)
            .param("recipientEmail", "recipient@example.com")
            .param("amount", "100.00")
            .param("description", "Test transaction"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transactions/tsuccessPage"));

        verify(mockTransactionService).sendMoney(userEmail, "recipient@example.com", new BigDecimal("100.00"), "Test transaction");
    }

    @Test
    public void testSendMoney_Failure() throws Exception {
        String userEmail = "user@example.com";
        doThrow(new RuntimeException("Error message")).when(mockTransactionService).sendMoney(anyString(), anyString(), any(BigDecimal.class), anyString());

        mockMvc.perform(post("/transactions/sendMoney")
            .principal(() -> userEmail)
            .param("recipientEmail", "recipient@example.com")
            .param("amount", "100.00")
            .param("description", "Test transaction"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transactions/terrorPage"));
    }

    @Test
    public void testTransactionHistory() throws Exception {
        String userEmail = "user@example.com";
        User user = new User();
        List<TransactionHistory> sentTransactions = new ArrayList<>();
        List<TransactionHistory> receivedTransactions = new ArrayList<>();
        

        when(mockUserService.findByEmail(userEmail)).thenReturn(user);
        when(mockTransactionService.findBySender(user)).thenReturn(sentTransactions);
        when(mockTransactionService.findByRecipient(user)).thenReturn(receivedTransactions);

        mockMvc.perform(get("/transactions/history").principal(() -> userEmail))
            .andExpect(status().isOk())
            .andExpect(model().attribute("sentTransactions", sentTransactions))
            .andExpect(model().attribute("receivedTransactions", receivedTransactions))
            .andExpect(view().name("historicTransaction"));
    }

    @Test
    public void testShowSuccessPage() throws Exception {
        mockMvc.perform(get("/transactions/tsuccessPage"))
            .andExpect(status().isOk())
            .andExpect(view().name("tsuccessPage"));
    }

    @Test
    public void testShowErrorPage() throws Exception {
        mockMvc.perform(get("/transactions/terrorPage"))
            .andExpect(status().isOk())
            .andExpect(view().name("terrorPage"));
    }
    
 

}

