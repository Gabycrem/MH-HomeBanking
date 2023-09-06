package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    public List<LoanDTO> getLoans();
    public Loan getLoanById(LoanApplicationDTO loanApplicationDTO);
    public boolean loanExistsById(LoanApplicationDTO loanApplicationDTO);
}
