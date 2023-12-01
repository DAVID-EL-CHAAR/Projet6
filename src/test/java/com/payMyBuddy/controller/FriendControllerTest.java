package com.payMyBuddy.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.FriendDTO;
import com.payMyBuddy.service.FriendService;
import com.payMyBuddy.service.UserService;

import org.mockito.junit.jupiter.MockitoExtension;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FriendControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FriendService mockFriendService;

    @Mock
    private UserRepository mockUserRepository;
    
    @Mock
    private UserService mockUserService;

    @Mock
    private Principal mockPrincipal;

    @Mock
    private Model mockModel;

    @InjectMocks
    private FriendController friendController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(friendController).build();
    }

    @Test
    public void testFreindList_withFriends() throws Exception {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        when(mockUserService.findByEmail(email)).thenReturn(mockUser);

        List<FriendDTO> friendsList = new ArrayList<>();
        friendsList.add(new FriendDTO("NomAmi", "PrenomAmi", "ami@example.com"));
        when(mockFriendService.getFriends(email)).thenReturn(friendsList);

        mockMvc.perform(get("/freindList").principal(() -> email))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friends"))
                .andExpect(view().name("friendlist"));
    }

    @Test
    public void testFreindList_noFriends() throws Exception {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        when(mockUserService.findByEmail(email)).thenReturn(mockUser);

        when(mockFriendService.getFriends(email)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/freindList").principal(() -> email))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("noFriendsMessage"))
                .andExpect(view().name("friendlist"));
    }
}



