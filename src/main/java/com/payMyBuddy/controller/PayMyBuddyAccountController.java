package com.payMyBuddy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;
import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.TransferService;

@Controller
@RequestMapping("/PMB")
public class PayMyBuddyAccountController {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private PayMyBuddyAccountRepository payMyBuddyAccountRepository;
		
	   @Autowired
	    private TransferService transferService;
    
	
	   @GetMapping("/accountDetails")
	   public String accountDetails(Model model, Principal principal) {
	       String email = principal.getName();
	       User user = userRepository.findByEmail(email);
	       
	       if (user != null) {
	           PayMyBuddyAccount account = payMyBuddyAccountRepository.findByUser(user);
	           model.addAttribute("account", account);

	           // Ajouter un attribut pour indiquer si le compte doit être activé
	           model.addAttribute("activateAccount", (account == null));
	       }

	       return "accountDetails";
	   }

	
	@PostMapping("/activatePayMyBuddyAccount")
	public ResponseEntity<?> activatePayMyBuddyAccount(Principal principal) {
	    try {
	        User user = userRepository.findByEmail(principal.getName());
	        if (user == null) {
	            return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
	        }

	        transferService.createAndLinkPayMyBuddyAccount(user);
	        return ResponseEntity.ok("Compte PayMyBuddy activé avec succès.");
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
	
	@GetMapping("/activatePayMyBuddyAccount")
	public ModelAndView showActivatePayMyBuddyAccountForm(Principal principal) {
	    ModelAndView modelAndView = new ModelAndView("PMBAccount");
	    User user = userRepository.findByEmail(principal.getName());

	    // Vérifier si l'utilisateur a déjà un compte PayMyBuddy
	    PayMyBuddyAccount existingAccount = payMyBuddyAccountRepository.findByUser(user);
	    if (existingAccount != null) {
	        modelAndView.addObject("accountAlreadyActivated", true);
	    } else {
	        modelAndView.addObject("accountAlreadyActivated", false);
	    }

	    return modelAndView;
	}



}
