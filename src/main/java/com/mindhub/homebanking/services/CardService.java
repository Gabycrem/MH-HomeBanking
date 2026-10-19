package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;

import java.util.Set;

public interface CardService {
    Set<CardDTO> getCards();
    boolean existsCardByNumber(String cardNumber);
    void saveCard(Card card);
}
