package com.mindhub.homebanking.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Account {

    //------------------- Propiedades ---------------------//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    private String number;
    private LocalDate creationDate;
    private BigDecimal balance;

    // -------------------Relaciones----------------//
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Transaction> transactions = new HashSet<>();

    // ---------------------Constructores -------------------//
    public Account(){
    }

    public Account(String number, LocalDate creationDate, BigDecimal balance){
        this.number = number;
        this.creationDate = creationDate;
        this.balance = balance;
    }

    //----------------- Getters y Setters------------------//
    public long getId() {
        return id;
    }

    public String getNumber(){
        return number;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public LocalDate getCreationDate(){
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate){
        this.creationDate = creationDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    // ---------- Métodos ADD y Otros ----------------- //

    public void addTransaction(Transaction transaction){
        transaction.setAccount(this);
        transactions.add(transaction);
    }
}
