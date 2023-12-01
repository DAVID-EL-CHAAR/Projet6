package com.payMyBuddy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.payMyBuddy.model.TransferHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.TransferService;
import com.payMyBuddy.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransferService mockTransferService;

    @Mock
    private UserRepository mockUserRepository;
    
    @Mock
    private UserService mockUserService;

    @InjectMocks
    private TransferController transferController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transferController)
                                 .setViewResolvers(viewResolver())
                                 .build();
    }

    private ViewResolver viewResolver() {
    	InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    	viewResolver.setPrefix("classpath:/templates/");
    	viewResolver.setSuffix(".html");
        return viewResolver;
    }
    
    @Test
    public void testTransferFromBankToPayMyBuddy_Success() throws Exception {
        String userEmail = "user@example.com";
        User user = new User(); // Assumez que User a un constructeur par défaut
        user.setEmail(userEmail);

        when(mockUserService.findByEmail(userEmail)).thenReturn(user);

        mockMvc.perform(post("/transfers/bankToPayMyBuddy")
            .principal(() -> userEmail)
            .param("rib", "123456789")
            .param("amount", "100.00"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfers/successPage"));
    }

    @Test
    public void testTransferFromBankToPayMyBuddy_Failure() throws Exception {
        String userEmail = "user@example.com";
        when(mockUserService.findByEmail(userEmail)).thenReturn(null);

        mockMvc.perform(post("/transfers/bankToPayMyBuddy")
            .principal(() -> userEmail)
            .param("rib", "123456789")
            .param("amount", "100.00"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfers/errorPage"));
    }

    
    @Test
    public void testTransferFromPayMyBuddyToBank_Success() throws Exception {
        String userEmail = "user@example.com";
        User user = new User();
        user.setEmail(userEmail);

        when(mockUserService.findByEmail(userEmail)).thenReturn(user);

        mockMvc.perform(post("/transfers/payMyBuddyToBank")
            .principal(() -> userEmail)
            .param("rib", "987654321")
            .param("amount", "50.00"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfers/successPage"));

        verify(mockTransferService).transferFromPayMyBuddyToBank(user, "987654321", new BigDecimal("50.00"));
    }

    @Test
    public void testTransferFromPayMyBuddyToBank_Failure_UserNotFound() throws Exception {
        String userEmail = "user@example.com";
        when(mockUserService.findByEmail(userEmail)).thenReturn(null);

        mockMvc.perform(post("/transfers/payMyBuddyToBank")
            .principal(() -> userEmail)
            .param("rib", "987654321")
            .param("amount", "50.00"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfers/errorPage"));
    }

    @Test
    public void testTransferHistory() throws Exception {
        String userEmail = "user@example.com";
        User user = new User();
        user.setEmail(userEmail);
        List<TransferHistory> history = new ArrayList<>();

        when(mockUserService.findByEmail(userEmail)).thenReturn(user);
        when(mockTransferService.getTransferHistory(user.getId())).thenReturn(history);

        mockMvc.perform(get("/transfers/transferHistory").principal(() -> userEmail))
            .andExpect(status().isOk())
            .andExpect(model().attribute("history", history))
            .andExpect(view().name("transferHistory"));
    }

    @Test
    public void testShowTransferFromBankToPayMyBuddyForm() throws Exception {
        mockMvc.perform(get("/transfers/bankToPayMyBuddy"))
            .andExpect(status().isOk())
            .andExpect(view().name("transferFromBankToPayMyBuddy"));
    }

    @Test
    public void testShowTransferFromPayMyBuddyToBankForm() throws Exception {
        mockMvc.perform(get("/transfers/payMyBuddyToBank"))
            .andExpect(status().isOk())
            .andExpect(view().name("transferFromPayMyBuddyToBank"));
    }

    @Test
    public void testShowSuccessPage() throws Exception {
        mockMvc.perform(get("/transfers/successPage"))
            .andExpect(status().isOk())
            .andExpect(view().name("successPage"));
    }

    @Test
    public void testShowErrorPage() throws Exception {
        mockMvc.perform(get("/transfers/errorPage"))
            .andExpect(status().isOk())
            .andExpect(view().name("errorPage"));
    }

    @Test
    public void testShowErrorPage2() throws Exception {
        mockMvc.perform(get("/transfers/errorPage2"))
            .andExpect(status().isOk())
            .andExpect(view().name("errorPage"));
    }

 
}

