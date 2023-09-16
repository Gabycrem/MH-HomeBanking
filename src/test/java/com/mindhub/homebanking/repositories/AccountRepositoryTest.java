package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void existsAccounts() {
        List<Account> accountList = accountRepository.findAll();
        assertThat(accountList,is(not(empty())));
    }

    @Test
    public void isNotNull() {
        List<Account> accountList = accountRepository.findAll();
        assertThat(accountList, hasItem(hasProperty("id", notNullValue() )));
    }
}