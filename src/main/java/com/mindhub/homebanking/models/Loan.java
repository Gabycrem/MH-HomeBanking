package com.mindhub.homebanking.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Loan {

    //------------------- Propiedades ---------------------//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal maxAmount;
    @ElementCollection
    @Column(name = "payment")
    private List<Integer> payments = new ArrayList<>();

    // ------ Relaciones -----------------------------------//
    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER)
    private Set<ClientLoan> clients = new HashSet<>();


    //-------------- Constructores ---------------------------//
    public Loan(){

    }

    public Loan(String name, BigDecimal maxAmount, List<Integer> payments){
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
    }

    //----------- Getters y Setters -------------------------------//
    public Long getId() {
        return id;
    }


    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }

    public Set<ClientLoan> getClientLoans() {
        return clients;
    }

    // ---------- Métodos ADD y Otros ----------------- //

    public void addClientLoans(ClientLoan clientLoan){
        clientLoan.setLoan(this);
        clients.add(clientLoan);
    }


    public List<Client> getClients(){
        return clients.stream().map(clientLoan -> clientLoan.getClient()).collect(Collectors.toList());
    }
}
