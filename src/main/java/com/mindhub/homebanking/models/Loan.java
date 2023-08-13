package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String name;
    private Double maxAmount;
    @ElementCollection
    @Column(name = "payment")
    private List<Integer> payments = new ArrayList<>();

    // ------ Relaciones -----------------------------------//
    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER)
    private Set<ClientLoan> clients = new HashSet<>();


    //-------------- Constructores ---------------------------//
    public Loan(){

    }

    public Loan(String name, Double maxAmount, List<Integer> payments){
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
    }

    //----------- Getters y Setters -------------------------------
    public Long getId() {
        return id;
    }


    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
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

    public void addClientLoans(ClientLoan clientLoan){
        clientLoan.setLoan(this);
        clients.add(clientLoan);
    }


    public List<Client> getClients(){
        return clients.stream().map(clientLoan -> clientLoan.getClient()).collect(Collectors.toList());
    }
}
