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
import com.payMyBuddy.service.UserService;

@Controller
@RequestMapping
public class FriendController {
    
	   private final FriendService friendService;
	    private final UserRepository userRepository;
	   
	    private UserService userService;

	    @Autowired
	    public FriendController(FriendService friendService, UserRepository userRepository, UserService userService) {
	        this.friendService = friendService;
	        this.userRepository = userRepository;
	        this.userService = userService;
	    }

    
    @GetMapping("/freindList")
    public String freindList(Model model, Principal principal) {
    	
        String email = principal.getName();
        
        // Trouve l'utilisateur par son email
        User user = userService.findByEmail(email);
        
        // Récupère la liste des amis de l'utilisateur
        List<FriendDTO> friendsList = friendService.getFriends(user.getEmail());
        
        if (friendsList != null && !friendsList.isEmpty()) {
            model.addAttribute("friends", friendsList);
        } else {
            
            model.addAttribute("noFriendsMessage", "Vous n'avez pas encore ajouté d'amis.");
        }
    
    
    return "friendlist";
        
    }

}

