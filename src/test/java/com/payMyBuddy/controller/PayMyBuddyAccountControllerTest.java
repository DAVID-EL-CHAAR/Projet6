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

import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.PayMyBuddyAccountService;
import com.payMyBuddy.service.TransferService;
import com.payMyBuddy.service.UserService;

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PayMyBuddyAccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository mockUserRepository;
    
    @Mock
    private UserService mockUserService;

    @Mock
    private PayMyBuddyAccountRepository mockPayMyBuddyAccountRepository;

    @Mock
    private PayMyBuddyAccountService mockPayMyBuddyAccountService;

    @Mock
    private TransferService mockTransferService;

    @InjectMocks
    private PayMyBuddyAccountController payMyBuddyAccountController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(payMyBuddyAccountController)
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
    public void testAccountDetails_UserExistsWithAccount() throws Exception {
        String userEmail = "user@example.com";
        User user = new User(); 
        user.setEmail(userEmail);
        PayMyBuddyAccount account = new PayMyBuddyAccount(); 

        when(mockUserService.findByEmail(userEmail)).thenReturn(user);
        when(mockPayMyBuddyAccountService.findByUser(user)).thenReturn(account);

        mockMvc.perform(get("/PMB/accountDetails").principal(() -> userEmail))
            .andExpect(status().isOk())
            .andExpect(model().attribute("account", account))
            .andExpect(view().name("accountDetails"));
    }

    
    @Test
    public void testActivatePayMyBuddyAccount_Success() throws Exception {
        String userEmail = "user@example.com";
        User user = new User(); 
        user.setEmail(userEmail);

        when(mockUserService.findByEmail(userEmail)).thenReturn(user);

        mockMvc.perform(post("/PMB/activatePayMyBuddyAccount").principal(() -> userEmail))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/PMB/activate"));
            
    }

    @Test
    public void testShowActivatePayMyBuddyAccountForm_AccountExists() throws Exception {
        String userEmail = "user@example.com";
        User user = new User(); 
        user.setEmail(userEmail);
        PayMyBuddyAccount account = new PayMyBuddyAccount(); 

        when(mockUserService.findByEmail(userEmail)).thenReturn(user);
        when(mockPayMyBuddyAccountService.findByUser(user)).thenReturn(account);

        mockMvc.perform(get("/PMB/activatePayMyBuddyAccount").principal(() -> userEmail))
            .andExpect(status().isOk())
            .andExpect(model().attribute("accountAlreadyActivated", true))
            .andExpect(view().name("PMBAccount"));
    }

    @Test
    public void testShowActivatePayMyBuddyAccountForm_NoAccount() throws Exception {
        String userEmail = "user@example.com";
        User user = new User(); 
        user.setEmail(userEmail);

        when(mockUserService.findByEmail(userEmail)).thenReturn(user);
        when(mockPayMyBuddyAccountService.findByUser(user)).thenReturn(null);

        mockMvc.perform(get("/PMB/activatePayMyBuddyAccount").principal(() -> userEmail))
            .andExpect(status().isOk())
            .andExpect(model().attribute("accountAlreadyActivated", false))
            .andExpect(view().name("PMBAccount"));
    }

    @Test
    public void testActivate() throws Exception {
        mockMvc.perform(get("/PMB/activate"))
            .andExpect(status().isOk())
            .andExpect(view().name("activate"));
    }

    
    
    
}

