package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;

import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

import static com.mindhub.homebanking.utils.Util.getRandomNumber;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;


    @GetMapping("/accounts")
    public Set<AccountDTO> getAccounts() {
        return accountService.getAccountsDto();
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
        return accountService.getAccountDtoById(id);
    }
    /*public AccountDTO getAccount(@PathVariable Long id, Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        Account accountId = accountRepository.findById(id).orElse(null);
        if (client.getAccounts().contains(accountId)){
            return accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
        }
        return new AccountDTO(null);
    }*/

    @GetMapping("/clients/current/accounts")
    public Set<AccountDTO> getClientCurrentAccounts(Authentication authentication){
        Client client = clientService.getClientByEmail(authentication.getName());
        return client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(toSet());
    }

    // -----------  CREACION DE NUEVA CUENTA DE CLIENTE LOGUEADO ----------------------//
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){
        Client client = clientService.getClientByEmail(authentication.getName());
        if (client.getAccounts().size() >= 3){
            return new ResponseEntity<>("The maximum number of accounts is 3.", HttpStatus.FORBIDDEN);
        }

        String numberAccount;
        do{
            numberAccount = "VIN"+getRandomNumber(10000000,99999999);
        } while (accountService.existsByNumber(numberAccount));

        Account account = new Account(numberAccount, LocalDate.now(),0.0);
        client.addAccount(account);
        accountService.saveAccount(account);
        return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
    }

}
