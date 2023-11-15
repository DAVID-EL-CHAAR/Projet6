package com.payMyBuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.security.Principal;
import com.payMyBuddy.model.BankAccount;
import com.payMyBuddy.service.BankAccountService;

@Controller
@RequestMapping("/bank-accounts")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService; // Assurez-vous d'avoir ce service implémenté

    // Afficher la liste des comptes bancaires
    @GetMapping
    public String listBankAccounts(Model model) {
        model.addAttribute("bankAccounts", bankAccountService.findAll());
        return "listAccount"; // Vue à créer
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
                              Principal principal,
                              RedirectAttributes redirectAttrs) {
     try {
         String userEmail = principal.getName(); // Obtient l'email de l'utilisateur connecté
         bankAccountService.addBankAccountToUser(userEmail, rib, initialBalance);
         redirectAttrs.addFlashAttribute("success", "Compte bancaire ajouté avec succès.");
     } catch (Exception e) {
         redirectAttrs.addFlashAttribute("error", "Erreur lors de l'ajout du compte bancaire : " + e.getMessage());
         return "redirect:/bank-accounts/add";
     }
     return "redirect:/bank-accounts";
 }



    // Afficher le formulaire pour modifier un compte bancaire
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("bankAccount", bankAccountService.findById(id));
        return "editAccount"; // Vue à créer
    }

    // Traiter la mise à jour d'un compte bancaire
    @PostMapping("/edit")
    public String updateBankAccount(@ModelAttribute BankAccount bankAccount) {
        bankAccountService.save(bankAccount);
        return "redirect:/bank-accounts";
    }

    // Supprimer un compte bancaire
    @GetMapping("/delete/{id}")
    public String deleteBankAccount(@PathVariable Long id) {
        bankAccountService.delete(id);
        return "redirect:/bank-accounts";
    }
}
