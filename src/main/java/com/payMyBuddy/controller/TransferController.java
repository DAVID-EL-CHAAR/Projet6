package com.payMyBuddy.controller;

import org.apache.el.stream.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.payMyBuddy.model.PayMyBuddyAccount;
import com.payMyBuddy.model.TransferHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.PayMyBuddyAccountRepository;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.TransferService;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private PayMyBuddyAccountRepository payMyBuddyAccountRepository;
	

    @PostMapping("/bankToPayMyBuddy")
    public ResponseEntity<?> transferFromBankToPayMyBuddy(Principal principal, 
                                                          @RequestParam String rib, 
                                                          @RequestParam BigDecimal amount) {
        try {
            User user = userRepository.findByEmail(principal.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
            }

            transferService.transferFromBankToPayMyBuddy(user, rib, amount);
            return ResponseEntity.ok("Transfert effectué avec succès.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


@PostMapping("/payMyBuddyToBank")
public ResponseEntity<?> transferFromPayMyBuddyToBank(Principal principal, 
                                                      @RequestParam String rib, 
                                                      @RequestParam BigDecimal amount) {
    try {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
        }

        transferService.transferFromPayMyBuddyToBank(user, rib, amount);
        return ResponseEntity.ok("Transfert effectué avec succès du compte PayMyBuddy au compte bancaire.");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    
}



@GetMapping("/transferHistory")
public ModelAndView transferHistory(Principal principal) {
    User user = userRepository.findByEmail(principal.getName());
    List<TransferHistory> history = transferService.getTransferHistory(user.getId());

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




}
    
