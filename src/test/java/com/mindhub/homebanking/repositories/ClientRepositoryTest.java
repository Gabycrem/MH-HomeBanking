package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
//import static org.hamcrest.Matchers.is;
@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;


    @Test
    public void passwordNotNull(){
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList, hasItem(hasProperty("password", is(notNullValue()))));
    }

    @Test
    public void emailIsCorrect(){
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList, hasItem(hasProperty("email", matchesPattern(".+@.+.com"))));
    }

}