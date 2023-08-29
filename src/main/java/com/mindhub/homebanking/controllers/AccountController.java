package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;

import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.utilitis.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;


    @GetMapping("/accounts")
    public Set<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toSet());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }

    // -----------  CREACION DE NUEVA CUENTA DE CLIENTE LOGUEADO ----------------------//
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client.getAccounts().size() >= 3){
            return new ResponseEntity<>("The maximum number of accounts is 3.", HttpStatus.FORBIDDEN);
        }

        Account account = new Account("VIN"+ Utils.getRandomAccountNumber(10000000,99999999), LocalDate.now(),0.0);
        client.addAccount(account);
        accountRepository.save(account);
        return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
    }

}
