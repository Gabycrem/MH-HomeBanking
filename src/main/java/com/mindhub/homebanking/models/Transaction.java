package com.mindhub.homebanking.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
public class Transaction {

    //------------------- Propiedades ---------------------//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;

    // -------------------Relaciones----------------//
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    // ---------------------Constructores -------------------//
    public Transaction(){

    }

    public Transaction(TransactionType type, BigDecimal amount, String description, LocalDateTime date){
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    //----------------- Getters y Setters------------------//
    public Long getId(){ return id;}

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
