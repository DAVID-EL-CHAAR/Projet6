package com.payMyBuddy.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


	@Entity
	public class TransactionHistory {
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "sender_id", nullable = false)
	    private User sender;

	    @ManyToOne
	    @JoinColumn(name = "recipient_id", nullable = false)
	    private User recipient;

	    @Column(nullable = false)
	    private BigDecimal amount;

	    @Column(nullable = false)
	    private LocalDateTime date;

	    @Column
	    private String description;
	    
	    public String getCommission() {
	        DecimalFormat df = new DecimalFormat("0.00");
	        return df.format(this.amount.multiply(BigDecimal.valueOf(0.005)));
	    }
	

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }
   

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    


}
