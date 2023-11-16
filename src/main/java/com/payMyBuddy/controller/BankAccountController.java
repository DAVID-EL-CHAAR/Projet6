package com.payMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import com.payMyBuddy.model.BankAccount;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.BankAccountService;

@Controller
@RequestMapping("/bank-accounts")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService; // Assurez-vous d'avoir ce service implémenté
    
    @Autowired
    private UserRepository userRepository;
   

    // Afficher la liste des comptes bancaires
    @GetMapping
    public String listBankAccounts(Model model, Principal principal) {
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail);
        List<BankAccount> bankAccounts = bankAccountService.findAllByUser(user);
        model.addAttribute("bankAccounts", bankAccounts);
        return "listAccount";
    }

    // Afficher le formulaire pour ajouter un nouveau compte bancaire
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("bankAccount", new BankAccount());
        return "addAccount"; // Vue à créer
    }


 // ...

    @PostMapping("/add")
    public String addBankAccount(@RequestParam("rib") String rib,
                                 @RequestParam("initialBalance") BigDecimal initialBalance,
                                 @RequestParam("nom") String nom,
                                 @RequestParam("prenom") String prenom,
                                 Principal principal,
                                 RedirectAttributes redirectAttrs) {
        try {
            String userEmail = principal.getName(); // Obtient l'email de l'utilisateur connecté
            bankAccountService.addBankAccountToUser(userEmail, rib, initialBalance, nom, prenom);
            redirectAttrs.addFlashAttribute("success", "Compte bancaire ajouté avec succès.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Erreur lors de l'ajout du compte bancaire : " + e.getMessage());
            return "redirect:/bank-accounts/add";
        }
        return "redirect:/bank-accounts";
    }




    // Afficher le formulaire pour modifier un compte bancaire
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        BankAccount bankAccount = bankAccountService.findById(id);
        if (bankAccount == null || !bankAccount.getUser().getEmail().equals(principal.getName())) {
            // Rediriger vers une page d'erreur ou afficher un message d'erreur
            return "redirect:/bank-accounts";
        }
        model.addAttribute("bankAccount", bankAccount);
        return "editAccount";
    }

    // Traiter la mise à jour d'un compte bancaire
    @PostMapping("/edit")
    public String updateBankAccount(@ModelAttribute BankAccount bankAccount) {
        bankAccountService.updateBankAccount(bankAccount.getId(), bankAccount.getRib(), bankAccount.getBalance(), bankAccount.getNom(), bankAccount.getPrenom());
        return "redirect:/bank-accounts";
    }


    // Supprimer un compte bancaire
    @GetMapping("/delete/{id}")
    public String deleteBankAccount(@PathVariable Long id, Principal principal) {
        BankAccount bankAccount = bankAccountService.findById(id);
        if (bankAccount != null && bankAccount.getUser().getEmail().equals(principal.getName())) {
            bankAccountService.delete(id);
        }
        // Gérer le cas où l'utilisateur n'est pas autorisé à supprimer le compte
        return "redirect:/bank-accounts";
    }
}
