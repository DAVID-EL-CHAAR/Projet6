package com.payMyBuddy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
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

public class UserControllerTest {

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
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        //OU ThymeleafViewResolver
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".html");

        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                                       .setViewResolvers(viewResolver)
                                       .build(); 
    }

    @Test
    public void registerUserAccount_ShouldRedirectToLoginOnSuccess() throws Exception {
    	mockMvc.perform(post("/register")
    		    .param("email", "user@example.com")
    		    .param("password", "Password123"))
    		    .andExpect(status().is3xxRedirection())
    		    .andExpect(redirectedUrl("/login"));
    }
    
    @Test
    public void registerUserAccount_ShouldRedirectToLoginOnSuccess2() throws Exception {
        when(userRepository.findByEmail("user@example.com")).thenReturn(null);

        mockMvc.perform(post("/register")
                .param("email", "user@example.com")
                .param("password", "Password123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        verify(userService).registerNewUserAccount(any(User.class));
    }
    
    /*
    @Test
    public void registerUserAccount_ShouldReturnRegistrationViewWhenUserExists() throws Exception {
        when(userRepository.findByEmail("user@example.com")).thenReturn(new User());

        mockMvc.perform(post("/register")
                .param("email", "user@example.com")
                .param("password", "Password123"))
            .andExpect(status().isOk())
            .andExpect(view().name("register"));
    }

*/


    @Test
    public void registerUserAccount_ShouldReturnRegistrationViewOnException() throws Exception {
        doThrow(new RuntimeException("User exists")).when(userService).registerNewUserAccount(any(User.class));

        mockMvc.perform(post("/register")
                .param("email", "user@example.com")
                .param("password", "password"))
            .andExpect(status().isOk())
            .andExpect(view().name("register"));
    }


    @Test
    public void showRegistrationForm_ShouldAddUserToModelAndRenderRegistrationView() throws Exception {
        mockMvc.perform(get("/register"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("user"))
            .andExpect(view().name("register"));
    }
    
    
    @Test
    public void testLoginView() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(view().name("login"));
    }
    
    @Test
    public void testShowAddFriendForm() throws Exception {
        mockMvc.perform(get("/addFriend"))
               .andExpect(view().name("addfriend"));
    }

    @Test
    public void testShowSuccessPage() throws Exception {
        mockMvc.perform(get("/FsuccessPage"))
               .andExpect(view().name("FsuccessPage"));
    }

    @Test
    public void testShowErrorPage() throws Exception {
        mockMvc.perform(get("/FerrorPage"))
               .andExpect(view().name("FerrorPage"));
    }
    
    @Test
    public void home_ShouldAddUserAndFriendsToModel_WhenUserExistsWithFriends() throws Exception {
        String userEmail = "user@example.com";
        User user = new User();
        user.setEmail(userEmail);

        // Créez une instance de FriendDTO 
        FriendDTO friendDTO = new FriendDTO("Nom", "Prénom", "friend@example.com");
        List<FriendDTO> friendsList = Collections.singletonList(friendDTO);

        when(userService.findByEmail(userEmail)).thenReturn(user);
        when(friendService.getFriends(userEmail)).thenReturn(friendsList);

        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(userEmail);

        mockMvc.perform(get("/home").principal(mockPrincipal))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("user"))
            .andExpect(model().attributeExists("friends"))
            .andExpect(view().name("home"));
    }
    
    


}