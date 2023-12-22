package com.payMyBuddy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.payMyBuddy.model.BankAccount;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.BankAccountService;
import com.payMyBuddy.service.UserService;


import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BankAccountService mockBankAccountService;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private UserService mockUserService;
    
    @InjectMocks
    private BankAccountController bankAccountController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bankAccountController).build();
    }

 

    
    @Test
    public void testListBankAccounts() throws Exception {
        String userEmail = "user@example.com";
        User user = new User();
        user.setEmail(userEmail);
        when(mockUserService.findByEmail(userEmail)).thenReturn(user);

        List<BankAccount> bankAccounts = new ArrayList<>();
        when(mockBankAccountService.findAllByUser(user)).thenReturn(bankAccounts);

        mockMvc.perform(get("/bank-accounts").principal(() -> userEmail))
            .andExpect(status().isOk())
            .andExpect(model().attribute("bankAccounts", bankAccounts))
            .andExpect(view().name("listAccount"));
    }

    
    @Test
    public void testShowAddForm() throws Exception {
        mockMvc.perform(get("/bank-accounts/add"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("bankAccount"))
            .andExpect(view().name("addAccount"));
    }

    
    @Test
    public void testAddBankAccount() throws Exception {
        String userEmail = "user@example.com";

        mockMvc.perform(post("/bank-accounts/add")
            .param("rib", "123456789")
            .param("initialBalance", "1000.00")
            .param("nom", "Nom")
            .param("prenom", "Prenom")
            .principal(() -> userEmail))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/bank-accounts"));
        
        
    }
    

    @Test
    public void testShowEditForm_AccountExists() throws Exception {
        Long accountId = 1L;
        String userEmail = "user@example.com";
        
        //  un utilisateur et définir l'e-mail
        User user = new User();
        user.setEmail(userEmail);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(accountId);
        bankAccount.setUser(user);

       

        when(mockBankAccountService.findById(accountId)).thenReturn(bankAccount);

        mockMvc.perform(get("/bank-accounts/edit/{accountId}", accountId).principal(() -> userEmail))
            .andExpect(status().isOk())
            .andExpect(model().attribute("bankAccount", bankAccount))
            .andExpect(view().name("editAccount"));
    }





    @Test
    public void testShowEditForm_AccountNotFound() throws Exception {
        Long accountId = 1L;
       

        // Configurer le service pour retourner null, simulant un compte non trouvé
        when(mockBankAccountService.findById(accountId)).thenReturn(null);

        
        mockMvc.perform(get("/bank-accounts/edit/{accountId}", accountId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/bank-accounts/errorPage3"))
            .andExpect(flash().attribute("errorMessage", "Compte bancaire non trouvé."));
    }





    @Test
    public void testUpdateBankAccount_Success() throws Exception {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setRib("123456789");
        bankAccount.setBalance(new BigDecimal("1000.00"));
        bankAccount.setNom("Nom");
        bankAccount.setPrenom("Prenom");

        mockMvc.perform(post("/bank-accounts/edit")
            .flashAttr("bankAccount", bankAccount))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/bank-accounts"));
    }

    @Test
    public void testUpdateBankAccount_Failure() throws Exception {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setRib("123456789");
        bankAccount.setBalance(new BigDecimal("1000.00"));
        bankAccount.setNom("Nom");
        bankAccount.setPrenom("Prenom");

        doThrow(new RuntimeException("Erreur de mise à jour")).when(mockBankAccountService).updateBankAccount(anyLong(), anyString(), any(), anyString(), anyString());

        mockMvc.perform(post("/bank-accounts/edit")
            .flashAttr("bankAccount", bankAccount))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/bank-accounts/errorPage3"));
            // Vous pouvez également vérifier si l'attribut de redirection errorMessage est ajouté si nécessaire
    }




@Test
public void testDeleteBankAccount_Success() throws Exception {
    Long accountId = 1L;
    String userEmail = "user@example.com";
    
    User user = new User();
    user.setEmail(userEmail);
    BankAccount bankAccount = new BankAccount();
    bankAccount.setUser(user);

    when(mockBankAccountService.findById(accountId)).thenReturn(bankAccount);

    mockMvc.perform(get("/bank-accounts/delete/{id}", accountId).principal(() -> userEmail))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/bank-accounts"));

    verify(mockBankAccountService).delete(accountId);
}


@Test
public void testDeleteBankAccount_Failure_NotFoundOrNotAuthorized() throws Exception {
	Long accountId = 1L;
    String userEmail = "user@example.com";
    
    User user = new User();
    user.setEmail(userEmail);
    BankAccount bankAccount = new BankAccount();
    bankAccount.setUser(user);
    // Configurer le service pour simuler un compte non trouvé
    when(mockBankAccountService.findById(accountId)).thenReturn(null);

    
    mockMvc.perform(get("/bank-accounts/delete/{accountId}", accountId))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/bank-accounts"));

    // Vérifier que la méthode delete n'a jamais été appelée
    verify(mockBankAccountService, never()).delete(accountId);
}



@Test
public void testShowErrorPage() throws Exception {
    mockMvc.perform(get("/bank-accounts/errorPage3"))
        .andExpect(status().isOk())
        .andExpect(view().name("aPageError"));
}

/*
 * @Test
public void testAddBankAccount_Success() throws Exception {
    // Configuration du mock pour un appel réussi
    doNothing().when(bankAccountService).addBankAccountToUser(anyString(), anyString(), any(), anyString(), anyString());

    String userEmail = "user@example.com";

    mockMvc.perform(post("/bank-accounts/add")
            .param("rib", "123456789")
            .param("initialBalance", "1000.00")
            .param("nom", "Nom")
            .param("prenom", "Prenom")
            .principal(() -> userEmail))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/bank-accounts"));

    // Vérification que la méthode a été appelée avec les bons paramètres
    verify(bankAccountService, times(1)).addBankAccountToUser(eq(userEmail), eq("123456789"), any(), eq("Nom"), eq("Prenom"));
}

@Test
public void testAddBankAccount_Exception() throws Exception {
    // Configuration du mock pour lancer une exception
    doThrow(new RuntimeException("Erreur")).when(bankAccountService).addBankAccountToUser(anyString(), anyString(), any(), anyString(), anyString());

    String userEmail = "user@example.com";

    mockMvc.perform(post("/bank-accounts/add")
            .param("rib", "123456789")
            .param("initialBalance", "1000.00")
            .param("nom", "Nom")
            .param("prenom", "Prenom")
            .principal(() -> userEmail))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/bank-accounts/errorPage3"));

    // Vérification que la méthode a été appelée
    verify(bankAccountService, times(1)).addBankAccountToUser(eq(userEmail), eq("123456789"), any(), eq("Nom"), eq("Prenom"));
}
 */


}

