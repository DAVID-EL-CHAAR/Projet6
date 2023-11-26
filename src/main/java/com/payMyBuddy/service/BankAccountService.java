package com.payMyBuddy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.payMyBuddy.model.BankAccount;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.BankAccountRepository;

import com.payMyBuddy.repository.UserRepository;

@Service
public class BankAccountService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@Transactional
	public void addBankAccountToUser(String userEmail, String rib, BigDecimal initialBalance, String nom, String prenom) throws Exception {
	    
	    // Vérifier si le RIB existe déjà
	    BankAccount existingAccount = bankAccountRepository.findByRib(rib);   
	    if (existingAccount != null) {
	        throw new Exception("Ce RIB existe déjà.");
	    }
	    
	    // Vérifier le solde initial
	    if (initialBalance.compareTo(new BigDecimal("10")) < 0) {
	        throw new Exception("Le solde initial doit être d'au moins 10€.");
	    }

	    // Trouver l'utilisateur par son email
	    User user = userRepository.findByEmail(userEmail);
	    if (user == null) {
	        throw new UsernameNotFoundException("Utilisateur non trouvé.");
	    }

	    // Créer et sauvegarder le nouveau compte bancaire
	    BankAccount bankAccount = new BankAccount();
	    bankAccount.setNom(nom);
	    bankAccount.setPrenom(prenom);
	    bankAccount.setRib(rib);
	    bankAccount.setBalance(initialBalance);
	    bankAccount.setUser(user);
	    bankAccountRepository.save(bankAccount);
	}

	@Transactional
	public void updateBankAccount(Long bankAccountId, String rib, BigDecimal newBalance, String nom, String prenom) throws Exception {
	    BankAccount existingAccount = bankAccountRepository.findByRib(rib);
	    if (existingAccount != null && !existingAccount.getId().equals(bankAccountId)) {
	        throw new Exception("Ce RIB existe déjà.");
	    }

	    if (newBalance.compareTo(new BigDecimal("10")) < 0) {
	        throw new Exception("Le solde initial doit être d'au moins 10€.");
	    }

	    Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById(bankAccountId);
	    if (!bankAccountOptional.isPresent()) {
	        throw new RuntimeException("Compte bancaire non trouvé.");
	    }

	    BankAccount bankAccount = bankAccountOptional.get();
	    bankAccount.setNom(nom);
	    bankAccount.setPrenom(prenom);
	    bankAccount.setRib(rib);
	    bankAccount.setBalance(newBalance);
	    bankAccountRepository.save(bankAccount);
	}



	// Récupérer tous les comptes bancaires
	@Transactional(readOnly = true)
    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }
    
	@Transactional(readOnly = true)
    public List<BankAccount> findAllByUser(User user) {
        return bankAccountRepository.findAllByUser(user);
    }

    // Trouver un compte bancaire par ID
	@Transactional(readOnly = true)
    public BankAccount findById(Long id) {
        Optional<BankAccount> result = bankAccountRepository.findById(id);
        return result.orElse(null); // Vous pouvez également gérer les exceptions ici
    }

    // Enregistrer un compte bancaire
	@Transactional
    public void save(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }

    // Supprimer un compte bancaire par ID
	@Transactional
    public void delete(Long id) {
        bankAccountRepository.deleteById(id);
    }


	
	
}
