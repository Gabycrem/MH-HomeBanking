package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;

    @Test
    public void cardExists(){
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList, is(not(empty())));
    }

    @Test
    public void cardColorOk(){
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList, hasItem(hasProperty("type", notNullValue(CardType.class))));
    }
}