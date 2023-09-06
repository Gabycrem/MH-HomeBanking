package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClientService {

    List<ClientDTO> getClientsDto();
    Client getClientCurrent(Authentication authentication);
    void saveClient(Client client);
    Client getClientByEmail (String email);
}
