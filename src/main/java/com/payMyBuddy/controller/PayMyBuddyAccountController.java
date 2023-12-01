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
import com.payMyBuddy.service.PayMyBuddyAccountService;
import com.payMyBuddy.service.TransferService;
import com.payMyBuddy.service.UserService;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/PMB")
public class PayMyBuddyAccountController {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private PayMyBuddyAccountRepository payMyBuddyAccountRepository;
	
	@Autowired
	private UserService userService;
		
	@Autowired
	private PayMyBuddyAccountService payMyBuddyAccountService;
	
	   @Autowired
	    private TransferService transferService;
    
	
	   @GetMapping("/accountDetails")
	   public String accountDetails(Model model, Principal principal) {
	       String email = principal.getName();
	       User user = userService.findByEmail(email);
	       
	       if (user != null) {
	           PayMyBuddyAccount account = payMyBuddyAccountService.findByUser(user);
	           model.addAttribute("account", account);

	           //  attribut pour indiquer si le compte doit être activé
	           model.addAttribute("activateAccount", (account == null));
	       }

	       return "accountDetails";
	   }

	
	   @PostMapping("/activatePayMyBuddyAccount")
	   public String activatePayMyBuddyAccount(Principal principal, RedirectAttributes redirectAttributes) {
	       try {
	           User user = userService.findByEmail(principal.getName());
	           if (user == null) {
	               redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé.");
	               return "redirect:/PMB/activate"; 
	           }

	           payMyBuddyAccountService.createAndLinkPayMyBuddyAccount(user);
	           redirectAttributes.addFlashAttribute("success", "Compte PayMyBuddy activé avec succès.");
	           return "redirect:/PMB/activate"; 
	       } catch (Exception e) {
	           redirectAttributes.addFlashAttribute("error", e.getMessage());
	           return "redirect:/PMB/activate"; 
	       }
	   }

	
	@GetMapping("/activatePayMyBuddyAccount")
	public ModelAndView showActivatePayMyBuddyAccountForm(Principal principal) {
	    ModelAndView modelAndView = new ModelAndView("PMBAccount");
	    User user = userService.findByEmail(principal.getName());

	    // Vérifier si l'utilisateur a déjà un compte PayMyBuddy
	    PayMyBuddyAccount existingAccount = payMyBuddyAccountService.findByUser(user);
	    if (existingAccount != null) {
	        modelAndView.addObject("accountAlreadyActivated", true);
	    } else {
	        modelAndView.addObject("accountAlreadyActivated", false);
	    }

	    return modelAndView;
	}
	
	@GetMapping("/activate")
	public String activate() {
	    return "activate"; 
	}





}
