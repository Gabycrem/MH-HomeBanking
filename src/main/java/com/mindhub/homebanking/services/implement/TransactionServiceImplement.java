package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class TransactionServiceImplement implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImplement(TransactionRepository transactionRepository) { this.transactionRepository = transactionRepository; }
    public Set<TransactionDTO> getTransactions() { return transactionRepository.findAll().stream().map(TransactionDTO::new).collect(toSet()); }
    public Transaction findById(Long id) { return transactionRepository.findById(id).orElse(null); }
    public void saveTransaction(Transaction transaction) { transactionRepository.save(transaction); }
}
