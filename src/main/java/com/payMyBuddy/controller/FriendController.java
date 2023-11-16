package com.payMyBuddy.controller;

import java.security.Principal;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.payMyBuddy.model.Friend;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.FriendDTO;
import com.payMyBuddy.service.FriendService;

@Controller
@RequestMapping
public class FriendController {
    
    @Autowired
    private FriendService friendService;
    
    private final UserRepository userRepository;

    @Autowired
    public FriendController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /*
    @PostMapping("/addFriend")
    public ResponseEntity<?> addFriend(Principal principal, @RequestParam String friendEmail) {
        try {
            friendService.addFriend(principal.getName(), friendEmail);
            return ResponseEntity.ok("ami ajouter avec succes");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

  
    @GetMapping("/addFriend")
    public String showAddFriendForm() {
        // Pas besoin d'ajouter de modèle si vous n'envoyez pas de données initiales à la vue
        return "addfriend"; // Le nom du fichier HTML sans l'extension .html
    }
    */
    
    /*
    
    @GetMapping("/friends")
    public ResponseEntity<List<Friend>> getFriends(Principal principal) {
        return ResponseEntity.ok(friendService.getFriends(principal.getName()));
    }
    */
    
    @GetMapping("/freindList")
    public String freindList(Model model, Principal principal) {
    	
        String email = principal.getName();
        
        // Trouve l'utilisateur par son email
        User user = userRepository.findByEmail(email);
        
        // Récupère la liste des amis de l'utilisateur
        List<FriendDTO> friendsList = friendService.getFriends(user.getEmail());
        
        if (friendsList != null && !friendsList.isEmpty()) {
            model.addAttribute("friends", friendsList);
        } else {
            // Si la liste des amis est vide ou nulle, on peut ajouter un message ou gérer autrement
            model.addAttribute("noFriendsMessage", "Vous n'avez pas encore ajouté d'amis.");
        }
    
    
    return "friendlist";
        
    }

}

