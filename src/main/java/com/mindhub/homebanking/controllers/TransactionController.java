package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;


    @GetMapping("/transaction")
    public Set<TransactionDTO> getTransactions(){
        return transactionService.getTransactions();
    }

    @GetMapping("/transaction/{id}")
    public TransactionDTO getTransaction(@PathVariable Long id){
        return new TransactionDTO(transactionService.findById(id));
    }


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam Double amount,
                                                    @RequestParam String description,
                                                    @RequestParam String fromAccountNumber,
                                                    @RequestParam String toAccountNumber,
                                                    Authentication authentication){

        Client client = clientService.getClientByEmail(authentication.getName());
        Account accountSource = accountService.getAccountByNumber(fromAccountNumber);
        Account accountDestination = accountService.getAccountByNumber(toAccountNumber);

        // --------------------- Validando que no haya campos vacios -------------------------------//
        if (amount == null){
            return new ResponseEntity<>("Amount is required", HttpStatus.FORBIDDEN);
        }
        if (description.isEmpty()){
            return new ResponseEntity<>("Description is required", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.isEmpty()){
            return new ResponseEntity<>("Soruce Account is required", HttpStatus.FORBIDDEN);
        }
        if (toAccountNumber.isEmpty()){
            return new ResponseEntity<>("Destination Account is required", HttpStatus.FORBIDDEN);
        }
        //  ------------- Validando que la cuenta de Origen Exista  ---------------------------------------//
        if (!accountService.existsByNumber(fromAccountNumber)){
            return new ResponseEntity<>("The source account does not exist", HttpStatus.FORBIDDEN);
        }

        //  ------------- Validando que la cuenta de Origen pertenezca al Cliente Autenticado ----------//
        if (!client.getAccounts().contains(accountSource)){
            return new ResponseEntity<>("The source account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }

        //  ------------- Validando que la cuenta de Destino Exista  ---------------------------------------//
        if (!accountService.existsByNumber(toAccountNumber)){
            return new ResponseEntity<>("The destination account does not exist", HttpStatus.FORBIDDEN);
        }

        //  ------------- Validando que la cuenta de Origen sea diferente a la de Destino --------------//
        if (fromAccountNumber.equals(toAccountNumber)){
            return new ResponseEntity<>("The destination account cannot be the source account", HttpStatus.FORBIDDEN);
        }

        //  ------------- Validando que la cuenta de Origen Tenga fondos suficientes ---------------------//
        if (amount > accountSource.getBalance()){
            return new ResponseEntity<>("Insufficient funds", HttpStatus.FORBIDDEN);
        }

        //  ------------------------------ Debitando de la cuenta de Origen ------------------------------//
        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, -amount, description, LocalDateTime.now());
        accountSource.addTransaction(debitTransaction);
        accountSource.setBalance(accountSource.getBalance() - amount);
        transactionService.saveTransaction(debitTransaction);
        accountService.saveAccount(accountSource);

        //  ------------------------------ Acreditando a la cuenta de Destino ------------------------------//
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());
        accountDestination.addTransaction(creditTransaction);
        accountDestination.setBalance(accountDestination.getBalance() +  amount);
        transactionService.saveTransaction(creditTransaction);
        accountService.saveAccount(accountDestination);
        return new ResponseEntity<>("Succesful transaction", HttpStatus.CREATED);
    }
}
