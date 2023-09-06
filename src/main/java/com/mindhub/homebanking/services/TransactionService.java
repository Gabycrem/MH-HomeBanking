package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Transaction;

import java.util.Set;

public interface TransactionService {
    public Set<TransactionDTO> getTransactions();
    public Transaction findById(Long id);
    public void saveTransaction(Transaction transaction);
}
