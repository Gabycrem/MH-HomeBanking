package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ClientServiceImplement implements ClientService {
    private final ClientRepository clientRepository;

    public ClientServiceImplement(ClientRepository clientRepository) { this.clientRepository = clientRepository; }
    public List<ClientDTO> getClientsDto() { return clientRepository.findAll().stream().map(ClientDTO::new).collect(toList()); }
    public Client getClientCurrent(Authentication authentication) { return clientRepository.findByEmail(authentication.getName()); }
    public void saveClient(Client client) { clientRepository.save(client); }
    public Client getClientByEmail(String email) { return clientRepository.findByEmail(email); }
}
