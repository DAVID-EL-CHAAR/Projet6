package com.payMyBuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;

import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public User registerNewUserAccount(User userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new RuntimeException("il y a un compte avec cette email " + userDto.getEmail());
        }
        User user = new User();
        user.setNom(userDto.getNom()); // Assurez-vous que le nom est aussi transféré
        user.setPrenom(userDto.getPrenom()); // Assurez-vous que le prénom est aussi transféré
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(true); // Activez le compte directement si c'est votre logique métier
        return userRepository.save(user);
    }

    
}


