package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.LoanService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LoanServiceImplement implements LoanService {
    private final LoanRepository loanRepository;

    public LoanServiceImplement(LoanRepository loanRepository) { this.loanRepository = loanRepository; }
    public List<LoanDTO> getLoans() { return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList()); }
    public Loan getLoanById(LoanApplicationDTO request) { return loanRepository.findById(request.getLoanId()).orElse(null); }
    public boolean loanExistsById(LoanApplicationDTO request) { return loanRepository.existsById(request.getLoanId()); }
}
