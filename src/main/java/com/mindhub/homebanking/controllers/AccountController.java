package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;

import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Set;

import static com.mindhub.homebanking.utils.Util.getRandomNumber;
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
    public ResponseEntity<?> getAccount(@PathVariable Long id, Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findById(id).orElse(null);

        if (account == null) {
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }

        // Compare identifiers, not entity object identity. Repository calls may return
        // detached instances from different persistence contexts.
        boolean belongsToClient = client != null && client.getAccounts().stream()
                .anyMatch(clientAccount -> clientAccount.getId() == account.getId());
        if (!belongsToClient) {
            return new ResponseEntity<>("The account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(new AccountDTO(account));
    }

    @GetMapping("/clients/current/accounts")
    public Set<AccountDTO> getClientCurrentAccounts(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        return client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(toSet());
    }

    // -----------  CREACION DE NUEVA CUENTA DE CLIENTE LOGUEADO ----------------------//
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client.getAccounts().size() >= 3){
            return new ResponseEntity<>("The maximum number of accounts is 3.", HttpStatus.FORBIDDEN);
        }

        String numberAccount;
        do{
            numberAccount = "VIN"+getRandomNumber(10000000,99999999);
        } while (accountRepository.existsByNumber(numberAccount));

        Account account = new Account(numberAccount, LocalDate.now(), BigDecimal.ZERO);
        client.addAccount(account);
        accountRepository.save(account);
        return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
    }

}
