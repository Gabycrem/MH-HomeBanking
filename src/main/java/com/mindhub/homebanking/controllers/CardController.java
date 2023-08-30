package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.CardUtil.randomCardNumber;
import static com.mindhub.homebanking.utils.Util.getRandomNumber;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/cards")
    public Set<CardDTO> cards(){
        return cardRepository.findAll().stream().map(card -> new CardDTO(card)).collect(Collectors.toSet());
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam CardType cardType ,@RequestParam CardColor cardColor, Authentication authentication){
        //Cliente Autenticado
        Client client = clientRepository.findByEmail(authentication.getName());

        //Lista de tarjetas del tipo solicitado.
        List<Card> cardsFiltered = client.getCards().stream().filter(card -> card.getType() == cardType).collect(Collectors.toList());

        // Valido que no tenga ya 3 cuentas
        if (cardsFiltered.size() >= 3){
            return new ResponseEntity<>("Maximum number of cards reached", HttpStatus.FORBIDDEN);
        }

        // Variable para guardar el numero de tarjeta
        String cardNumber;

        // Repito hasta que obtengo un numero de tarjeta que no esté en uso.
        do{
            cardNumber = randomCardNumber();
        } while (cardRepository.existsByNumber(cardNumber));

        //Creo nueva tarjeta
        Card newCard = new Card(client.getFirstName()+" "+client.getLastName(), cardType, cardColor,
                cardNumber, getRandomNumber(0,999), LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(newCard);
        cardRepository.save(newCard);
        return new ResponseEntity<>("Card created successfully", HttpStatus.CREATED);
    }
}
