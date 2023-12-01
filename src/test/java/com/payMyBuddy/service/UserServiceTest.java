package com.payMyBuddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerNewUserAccount_ShouldSaveUser_WhenEmailIsNe() {
    	User userDto = new User();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        //User userDtoo = new User();
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userDto);//l'objet retourne n'a pas d'importance sur la methode ni sur les valeur tester mias il faut utiliser any.class
        //on utilise anyclass si la methode cree un nouveau objet dedans
        User savedUser = userService.registerNewUserAccount(userDto);
     // on retourne userdto pour simplifier la methode et car il a des donner de l'objet user
        assertNotNull(savedUser);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("test@example.com", capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
        // Vérifiez les autres champs ici
    }
    
    @Test
    public void registerNewUserAccount_ShouldSaveUser_WhenEmailIsNew() {
        User userDto = new User();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        // Ajouter d'autres champs si nécessaire

        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userDto);

        User savedUser = userService.registerNewUserAccount(userDto);
                                                           
        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void registerNewUserAccount_ShouldThrowException_WhenEmailExists() {
        User userDto = new User();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        // Ajouter d'autres champs si nécessaire

        when(userRepository.findByEmail("test@example.com")).thenReturn(new User());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerNewUserAccount(userDto);
        });

        assertTrue(exception.getMessage().contains("il y a un compte avec cette email"));
    }
    
    @Test
    public void testFindByEmail() {
        // Given
        String email = "test@example.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(expectedUser);

        // When
        User resultUser = userService.findByEmail(email);

        // Then
        assertEquals(expectedUser, resultUser);
        verify(userRepository).findByEmail(email);
    }

   
}

