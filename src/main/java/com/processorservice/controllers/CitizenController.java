package com.processorservice.controllers;

import com.processorservice.models.dtos.*;
import com.processorservice.services.AuthMediumService;
import com.processorservice.services.EventService;
import com.processorservice.services.UserDetailsService;
import com.processorservice.services.UserService;
import com.processorservice.util.converters.CardConverter;
import com.processorservice.util.converters.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CitizenController {

    @Autowired
    private UserService userService;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    AuthMediumService authMediumService;
    @Autowired
    EventService eventService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());
        userService.register(registerRequest);
    }

    @PostMapping("/public/events")
    @ResponseStatus(HttpStatus.CREATED)
    public void triggerEvent(@RequestBody EventRequest eventRequest) {
        log.info("A new event was triggered with identificator: {}", eventRequest.getIdentificator());
        eventService.triggerEvent(eventRequest);
    }

    @GetMapping("/users")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getLoggedInUser() {
        log.info("Retrieving currently logged in user");
        return UserConverter.convertEntityToDto(userDetailsService.getCurrentlyLoggedUser());
    }

    @PostMapping("/users/cards")
    @PreAuthorize("hasRole('ROLE_CITIZEN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAuthMedium(@RequestBody CardDto cardDto) {
        log.info("Adding a new card with code: {}", cardDto.getCode());
        authMediumService.addAuthMedium(CardConverter.convertCardDtoToCard(cardDto));
    }

    @GetMapping("/users/cards")
    @PreAuthorize("hasRole('ROLE_CITIZEN')")
    @ResponseStatus(HttpStatus.OK)
    public List<CardDto> getAllCardsForUser() {
        log.info("Retrieving all the cards for logged in user.");
        return authMediumService.getAllCards().stream()
                .map(CardConverter::convertCardToCardDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/activities")
    @PreAuthorize("hasRole('ROLE_CITIZEN')")
    @ResponseStatus(HttpStatus.OK)
    public List<UserActivityDto> getAllActivityForUser() {
        log.info("Retrieving all the activity for logged in user.");
        return eventService.getAllActivitiesForUser();
    }
}
