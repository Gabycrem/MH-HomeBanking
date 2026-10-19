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

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.math.BigDecimal;
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
    public ResponseEntity<Object> createTransaction(@RequestParam BigDecimal amount,
                                                    @RequestParam String description,
                                                    @RequestParam String fromAccountNumber,
                                                    @RequestParam String toAccountNumber,
                                                    Authentication authentication){

        // --------------------- Validando que no haya campos vacios -------------------------------//
        if (amount == null){
            return new ResponseEntity<>("Amount is required", HttpStatus.FORBIDDEN);
        } else if (amount.signum() <= 0){ //Agregando validación de monto en 0 o negativo
            return new ResponseEntity<>("Amount must be greater than 0", HttpStatus.FORBIDDEN);
        }
        if (description == null || description.isBlank()){
            return new ResponseEntity<>("Description is required", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber == null || fromAccountNumber.isBlank()){
            return new ResponseEntity<>("Soruce Account is required", HttpStatus.FORBIDDEN);
        }
        if (toAccountNumber == null || toAccountNumber.isBlank()){
            return new ResponseEntity<>("Destination Account is required", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.equals(toAccountNumber)){
            return new ResponseEntity<>("The destination account cannot be the source account", HttpStatus.FORBIDDEN);
        }
        //  ------------- Validando que la cuenta de Origen Exista  ---------------------------------------//
        if (!accountRepository.existsByNumber(fromAccountNumber)){
            return new ResponseEntity<>("The source account does not exist", HttpStatus.FORBIDDEN);
        }

        if (!accountRepository.existsByNumber(toAccountNumber)){
            return new ResponseEntity<>("The destination account does not exist", HttpStatus.FORBIDDEN);
        }

        //  ------------- Validando que la cuenta de Origen pertenezca al Cliente Autenticado ----------//
        Client client = clientRepository.findByEmail(authentication.getName());
        Account accountSource;
        Account accountDestination;
        // Always lock accounts in the same order to reduce deadlocks when two transfers cross.
        if (fromAccountNumber.compareTo(toAccountNumber) < 0) {
            accountSource = accountRepository.getAccountByNumber(fromAccountNumber);
            accountDestination = accountRepository.getAccountByNumber(toAccountNumber);
        } else {
            accountDestination = accountRepository.getAccountByNumber(toAccountNumber);
            accountSource = accountRepository.getAccountByNumber(fromAccountNumber);
        }

        if (client == null || accountSource == null || accountDestination == null) {
            return new ResponseEntity<>("The requested account or client does not exist", HttpStatus.NOT_FOUND);
        }

        if (client.getAccounts().stream().noneMatch(account -> account.getId() == accountSource.getId())){
            return new ResponseEntity<>("The source account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }

        //  ------------- Validando que la cuenta de Origen Tenga fondos suficientes ---------------------//
        if (amount.compareTo(accountSource.getBalance()) > 0){
            return new ResponseEntity<>("Insufficient funds", HttpStatus.FORBIDDEN);
        }

        //  ------------------------------ Debitando de la cuenta de Origen ------------------------------//
        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, amount.negate(), description, LocalDateTime.now());
        accountSource.addTransaction(debitTransaction);
        accountSource.setBalance(accountSource.getBalance().subtract(amount));
        transactionRepository.save(debitTransaction);
        accountRepository.save(accountSource);

        //  ------------------------------ Acreditando a la cuenta de Destino ------------------------------//
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());
        accountDestination.addTransaction(creditTransaction);
        accountDestination.setBalance(accountDestination.getBalance().add(amount));
        transactionRepository.save(creditTransaction);
        accountRepository.save(accountDestination);
        return new ResponseEntity<>("Succesful transaction", HttpStatus.CREATED);
    }
}
