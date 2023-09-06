package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }

    @PostMapping("/loans")
    public ResponseEntity<Object> addLoans(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication){

        Client clientAuth = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.getAccountByNumber(loanApplicationDTO.getToAccountNumber());

        //Debe recibir un objeto de solicitud de crédito con los datos del préstamo
        Loan loan = loanRepository.findById(loanApplicationDTO.getLoanId()).orElse(null);
        Double amount = loanApplicationDTO.getAmount();
        Integer payments = loanApplicationDTO.getPayments();

        //Verificar que los datos sean correctos, es decir no estén vacíos, que el monto no sea 0 o que las cuotas no sean 0.
        if (loanApplicationDTO.getLoanId() == null){
            return new ResponseEntity<>("Missing data,loan is required", HttpStatus.FORBIDDEN);
        }
        //Verificar que el préstamo exista
        if(!loanRepository.existsById(loanApplicationDTO.getLoanId())){
            return new ResponseEntity<>("Missing data,loan is not Exists", HttpStatus.FORBIDDEN);
        }

        if (loanApplicationDTO.getAmount() <= 0){
            return new ResponseEntity<>("The amount must be greater than 0", HttpStatus.FORBIDDEN);
        } else if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) { //Verificar que el monto solicitado no exceda el monto máximo del préstamo
            return new ResponseEntity<>("The amount must not exceed $" + loan.getMaxAmount(), HttpStatus.FORBIDDEN);
        }

        //Verifica que la cantidad de cuotas se encuentre entre las disponibles del préstamo
        if (loanApplicationDTO.getPayments() <= 0){
            return new ResponseEntity<>("Payments is required", HttpStatus.FORBIDDEN);
        }

        //Verificar que se cargue una cuenta de destino
        if (loanApplicationDTO.getToAccountNumber().isEmpty()){
            return new ResponseEntity<>("Missing data,To Account Number is required", HttpStatus.FORBIDDEN);
        }

        //Verificar que la cuenta de destino exista
        if (!accountRepository.existsByNumber(account.getNumber())){
            return new ResponseEntity<>("This Account don't Exists ",HttpStatus.FORBIDDEN);
        }

        //Verificar que la cuenta de destino pertenezca al cliente autenticado
        if (!clientAuth.getAccounts().contains(account)){
            return new ResponseEntity<>("This Account don't belong to authentication client ",HttpStatus.FORBIDDEN);
        }

        //Se debe crear una solicitud de préstamo con el monto solicitado sumando el 20% del mismo
        ClientLoan clientLoan = new ClientLoan(amount*1.2, payments);
        loan.addClientLoans(clientLoan);
        clientAuth.addClientLoans(clientLoan);
        clientLoanRepository.save(clientLoan);

        //Se debe crear una transacción “CREDIT” asociada a la cuenta de destino (el monto debe quedar positivo) con la descripción concatenando el nombre del préstamo y la frase “loan approved”
        Transaction transactionCredit = new Transaction(TransactionType.CREDIT, amount, loan.getName() + " Loan approved", LocalDateTime.now());
        account.addTransaction(transactionCredit);
        //Se debe actualizar la cuenta de destino sumando el monto solicitado.
        account.setBalance(account.getBalance()+amount);
        transactionRepository.save(transactionCredit);
        accountRepository.save(account);




        return new ResponseEntity<>("Loan Acredi", HttpStatus.CREATED);
    }
}
