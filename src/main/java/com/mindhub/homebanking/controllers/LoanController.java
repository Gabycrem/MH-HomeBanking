package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientLoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    private static final BigDecimal LOAN_DEBT_FACTOR = new BigDecimal("1.20");

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final ClientLoanRepository clientLoanRepository;
    private final TransactionRepository transactionRepository;

    public LoanController(ClientRepository clientRepository,
                          AccountRepository accountRepository,
                          LoanRepository loanRepository,
                          ClientLoanRepository clientLoanRepository,
                          TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.loanRepository = loanRepository;
        this.clientLoanRepository = clientLoanRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanRepository.findAll().stream()
                .map(LoanDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/loans")
    @Transactional
    public ResponseEntity<?> applyForLoan(@RequestBody LoanApplicationDTO request,
                                          Authentication authentication) {
        if (request == null || request.getLoanId() == null || request.getAmount() == null
                || request.getPayments() == null || request.getToAccountNumber() == null
                || request.getToAccountNumber().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Loan, amount, payments and destination account are required");
        }

        Client client = clientRepository.findByEmail(authentication.getName());
        Loan loan = loanRepository.findById(request.getLoanId()).orElse(null);
        if (loan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0
                || request.getAmount().compareTo(loan.getMaxAmount()) > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("The amount must be greater than 0 and must not exceed the loan maximum");
        }

        if (!loan.getPayments().contains(request.getPayments())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The selected payments option is not available for this loan");
        }

        Account account = accountRepository.getAccountByNumber(request.getToAccountNumber());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Destination account not found");
        }

        if (client == null || account.getClient() == null
                || !Objects.equals(account.getClient().getId(), client.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Destination account does not belong to the authenticated client");
        }

        ClientLoan clientLoan = new ClientLoan(request.getAmount().multiply(LOAN_DEBT_FACTOR), request.getPayments());
        client.addClientLoans(clientLoan);
        loan.addClientLoans(clientLoan);
        clientLoanRepository.save(clientLoan);

        Transaction transaction = new Transaction(
                TransactionType.CREDIT,
                request.getAmount(),
                loan.getName() + " Loan approved",
                LocalDateTime.now());
        account.addTransaction(transaction);
        account.setBalance(account.getBalance().add(request.getAmount()));
        transactionRepository.save(transaction);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ClientLoanDTO(clientLoan));
    }
}
