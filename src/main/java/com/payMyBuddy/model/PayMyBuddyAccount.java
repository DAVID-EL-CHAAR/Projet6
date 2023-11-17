package com.payMyBuddy.model;

import java.math.BigDecimal;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToOne;

@Entity
public class PayMyBuddyAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void adjustBalance(BigDecimal amount, boolean isCredit) {
        if (isCredit) {
            this.balance = this.balance.add(amount);
        } else {
            this.balance = this.balance.subtract(amount);
        }
    }


    public Long getId() {
    	
    	return id;
    }
    
    public void setId(Long id) {
    	
    	 this.id = id;
    }
    
    public BigDecimal getBalance() {
    	
    	return balance;
    }
    
    public void setBalance(BigDecimal balance ) {
    	
    	this.balance = balance;
    }
    
    public User getUser() {
    	
    	return user;
    }
    
    public void setUser(User user){
    	
    	this.user = user;
    }
}

