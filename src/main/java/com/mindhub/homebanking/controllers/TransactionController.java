package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
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
    private TransactionRepository transactionRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/transaction")
    public Set<TransactionDTO> getTransactions(){
        return transactionRepository.findAll().stream().map(transaction -> new TransactionDTO(transaction)).collect(Collectors.toSet());
    }

    @GetMapping("/transaction/{id}")
    public TransactionDTO getTransaction(@PathVariable Long id){
        return new TransactionDTO(transactionRepository.findById(id).orElse(null));
    }


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam Double amount,
                                                    @RequestParam String description,
                                                    @RequestParam String fromAccountNumber,
                                                    @RequestParam String toAccountNumber,
                                                    Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName());
        Account accountSource = accountRepository.getAccountByNumber(fromAccountNumber);
        Account accountDestination = accountRepository.getAccountByNumber(toAccountNumber);

        // --------------------- Validando que no haya campos vacios -------------------------------//
        if (amount == null){
            return new ResponseEntity<>("Amount is required", HttpStatus.FORBIDDEN);
        } else if ( amount <= 0){ //Agregando validación de monto en 0 o negativo
            return new ResponseEntity<>("Amount must be greater than 0", HttpStatus.FORBIDDEN);
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
        if (!accountRepository.existsByNumber(fromAccountNumber)){
            return new ResponseEntity<>("The source account does not exist", HttpStatus.FORBIDDEN);
        }

        //  ------------- Validando que la cuenta de Origen pertenezca al Cliente Autenticado ----------//
        if (!client.getAccounts().contains(accountSource)){
            return new ResponseEntity<>("The source account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }

        //  ------------- Validando que la cuenta de Destino Exista  ---------------------------------------//
        if (!accountRepository.existsByNumber(toAccountNumber)){
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
        transactionRepository.save(debitTransaction);
        accountRepository.save(accountSource);

        //  ------------------------------ Acreditando a la cuenta de Destino ------------------------------//
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());
        accountDestination.addTransaction(creditTransaction);
        accountDestination.setBalance(accountDestination.getBalance() +  amount);
        transactionRepository.save(creditTransaction);
        accountRepository.save(accountDestination);
        return new ResponseEntity<>("Succesful transaction", HttpStatus.CREATED);
    }
}
