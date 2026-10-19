package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class AccountServiceImplement implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImplement(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Set<AccountDTO> getAccountsDto() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(toSet());
    }

    public AccountDTO getAccountDtoById(Long id) {
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }

    public boolean existsByNumber(String number) { return accountRepository.existsByNumber(number); }
    public void saveAccount(Account account) { accountRepository.save(account); }
    public Account getAccountByNumber(String number) { return accountRepository.getAccountByNumber(number); }
}
