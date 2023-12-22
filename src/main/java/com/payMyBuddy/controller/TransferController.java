package com.payMyBuddy.controller;

import org.apache.el.stream.Optional;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.TransferHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.TransferService;
import com.payMyBuddy.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private PayMyBuddyAccountRepository payMyBuddyAccountRepository;
    
    @Autowired
	private UserService userService;
	

    @PostMapping("/bankToPayMyBuddy")
    public String transferFromBankToPayMyBuddy(Principal principal, 
                                               @RequestParam String rib, 
                                               @RequestParam BigDecimal amount, 
                                               RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName());
            if (user == null) {
                return "redirect:/transfers/errorPage"; 
            }

            transferService.transferFromBankToPayMyBuddy(user, rib, amount);
            return "redirect:/transfers/successPage";
        } catch (Exception e) {
            
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/transfers/errorPage";
        }
    }


    @PostMapping("/payMyBuddyToBank")
    public String transferFromPayMyBuddyToBank(Principal principal, 
                                               @RequestParam String rib, 
                                               @RequestParam BigDecimal amount,
                                               RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName());
            if (user == null) {
                return "redirect:/transfers/errorPage";
            }

            transferService.transferFromPayMyBuddyToBank(user, rib, amount);
            return "redirect:/transfers/successPage";
        } catch (Exception e) {
           
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/transfers/errorPage";
        }
    }

/*
@GetMapping("/transferHistory")
public ModelAndView transferHistory(Principal principal) {
    User user = userService.findByEmail(principal.getName());
    List<TransferHistory> history = transferService.getTransferHistory(user.getId());

    ModelAndView modelAndView = new ModelAndView("transferHistory");
    modelAndView.addObject("history", history);
    return modelAndView;
}*/
    

@GetMapping("/transferHistory")
public ModelAndView transferHistory(Principal principal) {
    User user = userService.findByEmail(principal.getName());
    List<TransferHistory> history = transferService.getTransferHistory(user.getId());

    // Tri de la liste en ordre croissant de la date de transfert
    history.sort(Comparator.comparing(TransferHistory::getTransferDate).reversed());

    ModelAndView modelAndView = new ModelAndView("transferHistory");
    modelAndView.addObject("history", history);
    return modelAndView;
}


@GetMapping("/bankToPayMyBuddy")
public ModelAndView showTransferFromBankToPayMyBuddyForm() {
    return new ModelAndView("transferFromBankToPayMyBuddy");
}

@GetMapping("/payMyBuddyToBank")
public ModelAndView showTransferFromPayMyBuddyToBankForm() {
    return new ModelAndView("transferFromPayMyBuddyToBank");
}

@GetMapping("/successPage")
public String showSuccessPage() {
    return "successPage"; 
}

@GetMapping("/errorPage")
public String showErrorPage() {
    return "errorPage"; 
}

@GetMapping("/errorPage2")
public String showErrorPage2() {
    return "errorPage"; 
}





}
    
