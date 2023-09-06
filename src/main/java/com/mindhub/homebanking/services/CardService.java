package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;

import java.util.Set;
import java.util.stream.Collectors;

public interface CardService {

    public Set<CardDTO> getCards();
    boolean existsCardByNumber(String cardNumber);
    void saveCard(Card card);
}
