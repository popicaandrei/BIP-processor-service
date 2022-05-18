package com.processorservice.services;

import com.processorservice.models.entities.AuthMedium;
import com.processorservice.models.entities.Card;
import com.processorservice.models.entities.User;
import com.processorservice.repositories.AuthMediumRepository;
import com.processorservice.repositories.CardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.processorservice.models.enums.AuthType.CARD;


@Service
@Slf4j
public class AuthMediumService {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    AuthMediumRepository authMediumRepository;

    @Transactional
    public void addAuthMedium(Card card) {
        User user = userDetailsService.getCurrentlyLoggedUser();
        AuthMedium authMedium = new AuthMedium(null, CARD, card.getCode(), user);
        log.info("Saving new card with code: {}", card.getCode());
        cardRepository.save(card);
        log.info("Saving authMedium of type card for user with mail: {}, and identification: {}",
                authMedium.getUser().getEmail(), authMedium.getIdentificator());
        authMediumRepository.save(authMedium);
    }

    @Transactional(readOnly = true)
    public List<Card> getAllCards() {
        User user = userDetailsService.getCurrentlyLoggedUser();
        List<Card> cards = authMediumRepository.getAuthMediumByAuthTypeAndUser(CARD, user).stream()
                .map(authMedium -> cardRepository.findByCode(authMedium.getIdentificator()))
                .collect(Collectors.toList());
        log.info("Retrieving all cards for user: {}, found {} cards", user.getId(), cards.size());
        return cards;
    }

}
