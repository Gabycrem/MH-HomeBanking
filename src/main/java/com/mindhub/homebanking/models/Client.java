package com.mindhub.homebanking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Client {

    //------------------- Propiedades ---------------------//
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String firstName;
    private String lastName;
    private String email;


    // -------------------Relaciones----------------//
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<ClientLoan> loans = new HashSet<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Card> cards = new HashSet<>();


    // ---------------------Constructores -------------------//
    public Client() {
    }

    public Client(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    //----------------- Getters y Setters------------------//
    public Long getId(){
        return id;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public Set<ClientLoan> getClientLoans(){
        return loans;
    }

    public Set<Card> getCards() {
        return cards;
    }

    // ---------- Métodos ADD y Otros ----------------- //

    public void addAccount(Account account){
        account.setClient(this);
        accounts.add(account);
    }

    public void addClientLoans(ClientLoan clientLoan){
        clientLoan.setClient(this);
        loans.add(clientLoan);
    }

    public void addCard(Card card) {
        card.setClient(this);
        cards.add(card);
    }

    @JsonIgnore
    public List<Loan> getLoans(){
        return loans.stream().map(clientLoan -> clientLoan.getLoan()).collect(Collectors.toList());
    }


}
