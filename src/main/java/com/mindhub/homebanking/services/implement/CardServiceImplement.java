package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class CardServiceImplement implements CardService {
    private final CardRepository cardRepository;

    public CardServiceImplement(CardRepository cardRepository) { this.cardRepository = cardRepository; }
    public Set<CardDTO> getCards() { return cardRepository.findAll().stream().map(CardDTO::new).collect(toSet()); }
    public boolean existsCardByNumber(String cardNumber) { return cardRepository.existsByNumber(cardNumber); }
    public void saveCard(Card card) { cardRepository.save(card); }
}
