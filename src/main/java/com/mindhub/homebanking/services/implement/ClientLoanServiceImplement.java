package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import com.mindhub.homebanking.services.ClientLoanService;
import org.springframework.stereotype.Service;

@Service
public class ClientLoanServiceImplement implements ClientLoanService {
    private final ClientLoanRepository clientLoanRepository;

    public ClientLoanServiceImplement(ClientLoanRepository clientLoanRepository) {
        this.clientLoanRepository = clientLoanRepository;
    }

    public void saveClientLoan(ClientLoan clientLoan) { clientLoanRepository.save(clientLoan); }
}
