package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void descriptionNotNull(){
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList,is(not(empty())));
    }

    @Test
    public void amountNotZero(){
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList, hasItem(hasProperty("amount", not(equalTo(0.0)))));
    }
}