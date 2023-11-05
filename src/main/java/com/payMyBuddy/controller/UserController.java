package com.payMyBuddy.controller;

import java.net.URI;
import java.security.Principal;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.payMyBuddy.CustomUserDetails;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
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
    
  
/*
    // Endpoint pour l'inscription d'un nouvel utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerNewUserAccount(user);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(registeredUser.getId())
                    .toUri();
            return ResponseEntity.created(location).body("Utilisateur enregistré avec succès");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Ajoutez un nouvel objet User au modèle si vous utilisez Thymeleaf ou une technologie similaire pour le binding de formulaire
        model.addAttribute("user", new User());
        return "register 1234"; // Nom de la vue (par exemple, une page HTML Thymeleaf nommée 'register.html')
    }
    
    
*/
 // Endpoint pour l'inscription d'un nouvel utilisateur
    
    /*
    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register"; // Nom de la vue 
        }
        try {
            User registeredUser = userService.registerNewUserAccount(user);
            return "redirect:/user/login"; // Redirige vers la page de connexion après une inscription réussie
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register"; // Reste sur la page d'inscription si une erreur survient
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        
        model.addAttribute("user", new User());
        return "register"; // Nom de la vue (par exemple, une page HTML Thymeleaf nommée 'register.html')
    }
    
    */
    
    /*
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") User userDto) {
        userService.registerNewUserAccount(userDto);
        return "redirect:/login";
    }
    
    */
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


    @GetMapping("/login")
    public String login() {
        return "login";
    }
/*

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user, Model model) {
        try {
            // AuthenticationManager pour authentifier l'email et le mot de passe
            org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Rediriger vers la page d'accueil ou le tableau de bord de l'utilisateur après une connexion réussie
            return "redirect:/user/home";
        } catch (BadCredentialsException e) {
           
            model.addAttribute("loginError", "Échec de la connexion: Identifiants incorrects");
            return "login";
        }
    }

    // La méthode pour afficher le formulaire de connexion
    @GetMapping("/login")
    public String loginForm(Model model) {
        // Assurez-vous que l'objet User est ajouté au modèle pour le binding de formulaire
        model.addAttribute("user", new User());
        return "login"; // Nom de la vue pour la page de connexion
    }

*/
 
/*
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal User user) {
        
        model.addAttribute("user", user);
        
        // Retourne le nom de la vue pour la page d'accueil
        return "home"; 
    }

    */
    /*
    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        // Récupérez l'email depuis le Principal, qui est fourni par Spring Security
        String email = principal.getName();
        // Utilisez le UserRepository pour charger l'entité User complète
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // Gérez l'erreur si l'utilisateur n'est pas trouvé, éventuellement en renvoyant une vue d'erreur.
            return "error";
        }
        // Ajoutez l'utilisateur au modèle pour l'utiliser dans la vue
        model.addAttribute("user", user);
        return "home";
    }

   */
   /* @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            User user = userDetails.getUser(); // Si CustomUserDetails contient une référence à votre entité User
            model.addAttribute("user", user);
        }
        return "home"; // Retourne le nom de la vue Thymeleaf
    }

*/
    
    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        String email = principal.getName(); // Récupère le nom (email) de l'utilisateur connecté
        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);
        return "home";
    }
 
}

