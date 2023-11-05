package com.payMyBuddy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.payMyBuddy.model.User;

import java.util.Collection;
import java.util.Collections;



public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ici vous pouvez retourner les autorités/roles de l'utilisateur si nécessaire.
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); 
    }

    // Les méthodes suivantes peuvent retourner des valeurs fixes si vous ne gérez pas ces propriétés
    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled(); // Assurez-vous que votre entité User a cette propriété
    }

    // Getter pour accéder à l'utilisateur complet depuis l'objet UserDetails si nécessaire
    public User getUser() {
        return user;
    }
    
    
}

