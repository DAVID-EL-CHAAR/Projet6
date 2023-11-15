package com.payMyBuddy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.payMyBuddy.model.Friend;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.FriendRepository;
import com.payMyBuddy.repository.UserRepository;

@Service
public class FriendService {
    
    @Autowired
    private FriendRepository friendRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public void addFriend(String userEmail, String friendEmail) throws Exception {
        if (userEmail.equalsIgnoreCase(friendEmail)) {
            throw new Exception("Vous ne pouvez pas vous ajouter vous-même comme ami");
        }

        User user = userRepository.findByEmail(userEmail);
        User friendUser = userRepository.findByEmail(friendEmail);

        if (user == null || friendUser == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé.");
        }

        if (friendRepository.findByUserAndFriendEmail(user, friendEmail) != null ||
            friendRepository.findByUserAndFriendEmail(friendUser, userEmail) != null) {
            throw new Exception("Cet ami a déjà été ajouté");
        }

        
        Friend friendForUser = new Friend();
        friendForUser.setUser(user);
        friendForUser.setFriendEmail(friendEmail);
        friendRepository.save(friendForUser);

        
        Friend friendForFriendUser = new Friend();
        friendForFriendUser.setUser(friendUser);
        friendForFriendUser.setFriendEmail(userEmail);
        friendRepository.save(friendForFriendUser);
    }
    
    public List<Friend> getFriends(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User non trouve avec cette email: " + userEmail);
        }
        return friendRepository.findAllByUser(user);
    }
}
