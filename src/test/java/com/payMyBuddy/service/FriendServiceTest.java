package com.payMyBuddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.payMyBuddy.model.Friend;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.FriendRepository;
import com.payMyBuddy.repository.UserRepository;

public class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendService friendService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addFriend_ShouldThrowException_WhenAddingSelf() {
        String userEmail = "user@example.com";
        assertThrows(Exception.class, () -> {
            friendService.addFriend(userEmail, userEmail);
        });
    }

    @Test
    public void addFriend_ShouldThrowException_WhenUserOrFriendNotExist() {
        String userEmail = "user@example.com";
        String friendEmail = "friend@example.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            friendService.addFriend(userEmail, friendEmail);
        });
    }

    @Test
    public void addFriend_ShouldThrowException_WhenFriendAlreadyAdded() {
        String userEmail = "user@example.com";
        String friendEmail = "friend@example.com";
        User user = new User();
        User friend = new User();

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(userRepository.findByEmail(friendEmail)).thenReturn(friend);
        when(friendRepository.findByUserAndFriendEmail(user, friendEmail)).thenReturn(new Friend());

        assertThrows(Exception.class, () -> {
            friendService.addFriend(userEmail, friendEmail);
        });
    }

    //methode principal 
    @Test
    public void addFriend_ShouldAddFriend_WhenValidRequest() throws Exception {
        String userEmail = "user@example.com";
        String friendEmail = "friend@example.com";
        User user = new User();
        User friend = new User();

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(userRepository.findByEmail(friendEmail)).thenReturn(friend);
        when(friendRepository.findByUserAndFriendEmail(user, friendEmail)).thenReturn(null);
        when(friendRepository.findByUserAndFriendEmail(friend, userEmail)).thenReturn(null);

        friendService.addFriend(userEmail, friendEmail);

        verify(friendRepository, times(2)).save(any(Friend.class));
    }
    /*
    @Test
    public void addFriend_ShouldAddFriend_WhenValidRequest() throws Exception {
        // Initialisation
        String userEmail = "user@example.com";
        String friendEmail = "friend@example.com";
        User user = new User();
        User friend = new User();

        // Configuration des mocks
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(userRepository.findByEmail(friendEmail)).thenReturn(friend);
        when(friendRepository.findByUserAndFriendEmail(user, friendEmail)).thenReturn(null);
        when(friendRepository.findByUserAndFriendEmail(friend, userEmail)).thenReturn(null);

        // ArgumentCaptor pour capturer les objets Friend enregistrés
        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);

        // Appel de la méthode testée
        friendService.addFriend(userEmail, friendEmail);

        // Vérification que la méthode save est appelée deux fois avec des objets Friend
        verify(friendRepository, times(2)).save(friendCaptor.capture());

        // Récupération des objets Friend capturés
        List<Friend> capturedFriends = friendCaptor.getAllValues();

        // Assertions pour vérifier les détails des objets Friend
        assertEquals(2, capturedFriends.size(), "Deux objets Friend doivent être enregistrés");
        assertEquals(userEmail, capturedFriends.get(0).getUser().getEmail());
        assertEquals(friendEmail, capturedFriends.get(0).getFriendEmail());
        assertEquals(friendEmail, capturedFriends.get(1).getUser().getEmail());
        assertEquals(userEmail, capturedFriends.get(1).getFriendEmail());
    }

*/
    
    
    @Test
    public void getFriends_ShouldThrowException_WhenUserNotExist() {
        String userEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            friendService.getFriends(userEmail);
        });
    }

    @Test
    public void getFriends_ShouldReturnListOfFriendDTOs_WhenUserExists() {
        String userEmail = "user@example.com";
        User user = new User(); 
        

        Friend friend1 = new Friend();
        friend1.setFriendEmail("friend1@example.com");

        Friend friend2 = new Friend();
        friend2.setFriendEmail("friend2@example.com");

        User friendUser1 = new User(); // Crée un objet User pour friend1
        friendUser1.setNom("NomFriend1");
        friendUser1.setPrenom("PrenomFriend1");

        User friendUser2 = new User(); // Crée un objet User pour friend2
        friendUser2.setNom("NomFriend2");
        friendUser2.setPrenom("PrenomFriend2");

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(friendRepository.findAllByUser(user)).thenReturn(Arrays.asList(friend1, friend2));
        when(userRepository.findByEmail("friend1@example.com")).thenReturn(friendUser1);
        when(userRepository.findByEmail("friend2@example.com")).thenReturn(friendUser2);

        List<FriendDTO> friendDTOs = friendService.getFriends(userEmail);

        assertNotNull(friendDTOs);
        assertEquals(2, friendDTOs.size());
        
    }


    /* 
     * @Test
public void getFriends_ShouldReturnListOfFriendDTOs_WhenUserExists() {
    // Configuration initiale
    String userEmail = "user@example.com";
    User user = new User(); // Configurez l'utilisateur selon vos besoins

    Friend friend1 = new Friend();
    friend1.setFriendEmail("friend1@example.com");

    Friend friend2 = new Friend();
    friend2.setFriendEmail("friend2@example.com");

    User friendUser1 = new User(); // Créez un objet User pour friend1
    friendUser1.setNom("NomFriend1");
    friendUser1.setPrenom("PrenomFriend1");

    User friendUser2 = new User(); // Créez un objet User pour friend2
    friendUser2.setNom("NomFriend2");
    friendUser2.setPrenom("PrenomFriend2");

    // Configuration des mocks
    when(userRepository.findByEmail(userEmail)).thenReturn(user);
    when(friendRepository.findAllByUser(user)).thenReturn(Arrays.asList(friend1, friend2));
    when(userRepository.findByEmail("friend1@example.com")).thenReturn(friendUser1);
    when(userRepository.findByEmail("friend2@example.com")).thenReturn(friendUser2);

    // Appel à la méthode testée
    List<FriendDTO> friendDTOs = friendService.getFriends(userEmail);

    // Assertions
    assertNotNull(friendDTOs, "La liste des FriendDTO ne devrait pas être null");
    assertEquals(2, friendDTOs.size(), "La liste des FriendDTO devrait contenir deux éléments");

    FriendDTO dto1 = friendDTOs.get(0);
    FriendDTO dto2 = friendDTOs.get(1);

    // Assertions pour le premier FriendDTO
    assertEquals("friend1@example.com", dto1.getEmail(), "L'email du premier FriendDTO devrait correspondre");
    assertEquals("NomFriend1", dto1.getNom(), "Le nom du premier FriendDTO devrait correspondre");
    assertEquals("PrenomFriend1", dto1.getPrenom(), "Le prénom du premier FriendDTO devrait correspondre");

    // Assertions pour le second FriendDTO
    assertEquals("friend2@example.com", dto2.getEmail(), "L'email du second FriendDTO devrait correspondre");
    assertEquals("NomFriend2", dto2.getNom(), "Le nom du second FriendDTO devrait correspondre");
    assertEquals("PrenomFriend2", dto2.getPrenom(), "Le prénom du second FriendDTO devrait correspondre");
}

     */
   
}

