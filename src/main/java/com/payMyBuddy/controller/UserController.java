package com.payMyBuddy.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import javax.naming.AuthenticationException;

import org.apache.maven.artifact.repository.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.payMyBuddy.CustomUserDetails;
import com.payMyBuddy.model.Friend;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.FriendDTO;
import com.payMyBuddy.service.FriendService;
import com.payMyBuddy.service.UserService;


@Controller
@RequestMapping
public class UserController {

	
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FriendService friendService;
    
  

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") User userDto, BindingResult result) {
        try {
            userService.registerNewUserAccount(userDto);
        } catch (RuntimeException e) {
            result.rejectValue("email", "user.exists", e.getMessage());
            return "register";
        }
        return "redirect:/login";
    }

    @PostMapping("/addFriend")
    public String addFriend(Principal principal, @RequestParam String friendEmail, RedirectAttributes redirectAttributes) {
        try {
            friendService.addFriend(principal.getName(), friendEmail);
            return "redirect:/FsuccessPage";
        } catch (Exception e) {
           
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/FerrorPage";
        }
    }



    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/addFriend")
    public ModelAndView showTransferFromPayMyBuddyToBankForm() {
        return new ModelAndView("addfriend");
    }
    
    @GetMapping("/FsuccessPage")
    public String showSuccessPage() {
        return "FsuccessPage"; 
    }

    @GetMapping("/FerrorPage")
    public String showErrorPage() {
        return "FerrorPage"; 
    }


    
    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        // Récupère l'email de l'utilisateur connecté
        String email = principal.getName();
        
        // Trouve l'utilisateur par son email
        User user = userRepository.findByEmail(email);
        
        // Assurez-vous que l'utilisateur existe
        if (user != null) {
            model.addAttribute("user", user);
            
            // Récupère la liste des amis de l'utilisateur
            List<FriendDTO> friendsList = friendService.getFriends(user.getEmail());
            
            // Vérifie si la liste des amis n'est pas vide
            if (friendsList != null && !friendsList.isEmpty()) {
                model.addAttribute("friends", friendsList);
            } else {
                // Si la liste des amis est vide ou nulle
                model.addAttribute("noFriendsMessage", "Vous n'avez pas encore ajouté d'amis.");
            }
        } else {
            // Si l'utilisateur n'est pas trouvé, 
            model.addAttribute("errorMessage", "Utilisateur non trouvé.");
            // on peut également rediriger vers une page d'erreur ou d'accueil
            // return "errorPage"; 
        }
        
        // Retourne le nom de la vue à afficher
        return "home";
    }

 
}

