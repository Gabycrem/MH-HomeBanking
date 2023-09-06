package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;

import java.util.Set;

public interface AccountService {


    Set<AccountDTO> getAccountsDto();
    AccountDTO getAccountDtoById(Long id);
    boolean existsByNumber(String number);

    void saveAccount(Account account);
}
