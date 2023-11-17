package com.payMyBuddy.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.TransactionHistory;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.TransactionHistoryRepository;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.TransactionService;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @GetMapping("/sendMoney")
    public ModelAndView showSendMoneyForm() {
        // Ajouter des logiques nécessaires si besoin
        return new ModelAndView("sendMoney");
    }

    @PostMapping("/sendMoney")
    public ResponseEntity<?> sendMoney(Principal principal,
                                       @RequestParam String recipientEmail,
                                       @RequestParam BigDecimal amount,
                                       @RequestParam(required = false) String description) {
        try {
            transactionService.sendMoney(principal.getName(), recipientEmail, amount, description);
            return ResponseEntity.ok("Transaction réussie.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @GetMapping("/history")
    public ModelAndView transactionHistory(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            return new ModelAndView("errorPage");
        }

        List<TransactionHistory> sentTransactions = transactionHistoryRepository
            .findBySender(user);

        List<TransactionHistory> receivedTransactions = transactionHistoryRepository
            .findByRecipient(user);

        ModelAndView modelAndView = new ModelAndView("historicTransaction");
        modelAndView.addObject("sentTransactions", sentTransactions);
        modelAndView.addObject("receivedTransactions", receivedTransactions);
        return modelAndView;
    }




    @GetMapping("/sent")
    public ResponseEntity<List<Transaction>> getSentTransactions(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        List<Transaction> sentTransactions = transactionService.getSentTransactions(user.getId());
        return ResponseEntity.ok(sentTransactions);
    }

    @GetMapping("/received")
    public ResponseEntity<List<Transaction>> getReceivedTransactions(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        List<Transaction> receivedTransactions = transactionService.getReceivedTransactions(user.getId());
        return ResponseEntity.ok(receivedTransactions);
    }


  
}


