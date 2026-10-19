package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Transaction;

import java.util.Set;

public interface TransactionService {
    Set<TransactionDTO> getTransactions();
    Transaction findById(Long id);
    void saveTransaction(Transaction transaction);
}
