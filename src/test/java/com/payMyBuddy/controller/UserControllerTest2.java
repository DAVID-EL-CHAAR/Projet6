package com.payMyBuddy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.payMyBuddy.model.User;
import com.payMyBuddy.service.CustomUserDetailsService;
import com.payMyBuddy.service.FriendDTO;
import com.payMyBuddy.service.FriendService;
import com.payMyBuddy.service.UserService;
import com.payMyBuddy.controller.UserController;
import com.payMyBuddy.repository.FriendRepository;
import com.payMyBuddy.repository.UserRepository;

public class UserControllerTest2 {

    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private FriendService friendService;
    
    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".html");

        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                                       .setViewResolvers(viewResolver)
                                       .build(); 
    }


    @Test
    public void testAddFriendSuccess() throws Exception {
        String principalName = "user@example.com";
        String friendEmail = "friend@example.com";

        doNothing().when(friendService).addFriend(principalName, friendEmail);
        //do ntohing simule que la methode reussi sans erreur, et on l'utilise quand la methode est void(ne retourne rien)
        mockMvc.perform(post("/addFriend")
                .principal(() -> principalName)//expression lambda
                .param("friendEmail", friendEmail))
                .andExpect(redirectedUrl("/FsuccessPage"));

        verify(friendService, times(1)).addFriend(principalName, friendEmail);
    }

    @Test
    public void testAddFriendFailure() throws Exception {
        String principalName = "user@example.com";
        String friendEmail = "friend@example.com";
        String errorMessage = "Error message";

        doThrow(new RuntimeException(errorMessage)).when(friendService).addFriend(principalName, friendEmail);

        mockMvc.perform(post("/addFriend")
                .principal(() -> principalName)
                .param("friendEmail", friendEmail))
                .andExpect(redirectedUrl("/FerrorPage"));

        verify(friendService, times(1)).addFriend(principalName, friendEmail);
    }
    
    @Test
    public void addFriend_ShouldRedirectToErrorPageOnException() throws Exception {
        doThrow(new Exception("Error adding friend")).when(friendService).addFriend(anyString(), anyString());

        mockMvc.perform(post("/addFriend").param("friendEmail", "friend@example.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/FerrorPage"));
    
}
}
