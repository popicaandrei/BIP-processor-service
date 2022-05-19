package com.processorservice.services;

import com.processorservice.config.exceptions.EventDataException;
import com.processorservice.models.dtos.EventRequest;
import com.processorservice.models.entities.*;
import com.processorservice.models.enums.AuthType;
import com.processorservice.models.enums.RoleType;
import com.processorservice.models.messaging.EventPayload;
import com.processorservice.repositories.EventRegistryRepository;
import com.processorservice.repositories.EventRepository;
import com.processorservice.util.converters.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    EventRegistryRepository eventRegistryRepository;
    @Autowired
    AuthMediumService authMediumService;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    InstitutionService institutionService;
    @Autowired
    UserService userService;

    public Event getEventByName(String name) {
        log.info("Getting event with name: {}", name);
        return eventRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    public Event getActiveEventByName(String name) {
        log.info("Getting active event with name: {}", name);
        return eventRepository.findEventByNameAndActive(name, true)
                .orElseThrow(() -> new EntityNotFoundException("Active event not found"));
    }

    public void addEvent(Event event) {
        Institution institution = institutionService.getInstitutionByRepresentative();
        event.setInstitution(institution);
        log.info("Adding new event with id: {}, for institution with id: {}", event.getId(), institution.getId());
        eventRepository.save(event);
    }

    public List<Event> getAllEventsByInstitution() {
        return eventRepository.findAllByInstitution(institutionService.getInstitutionByRepresentative());
    }

    @Transactional
    public void triggerEvent(EventRequest eventRequest) {
        EventRegistry eventRegistry;
        Event event = getActiveEventByName(eventRequest.getEventName());
        AuthMedium authMedium = authMediumService.getByIdentificator(eventRequest.getIdentificator());
        User user = authMedium.getUser();

        verifyAuthMedium(eventRequest, user, authMedium, event);

        log.info("Event request is triggered for user with email: {}, using authType: {}, with identificator {}," +
                        " event name: {}, reward is {}, assigned institution: {}",
                user.getEmail(), authMedium.getAuthType(), authMedium.getIdentificator(),
                event.getName(), event.getReward(), user.getInstitution().getName());

        if (event.isValidationNeeded()) {
            log.info("Further validation is needed, the event is not sent to be rewarded");
            eventRegistry = createEventRegistry(event, user, false);
        } else {
            EventPayload eventPayload = createMessagePayload(event, user, authMedium, user.getInstitution());
            log.info("Message payload is created in order to be sent for reward");
            eventRegistry = createEventRegistry(event, user, true);
            //TODO: send to rabbit
        }
        eventRegistryRepository.save(eventRegistry);
    }

    @Transactional
    public void validateEvent(Integer eventRegistryId) {
        EventRegistry eventRegistry = eventRegistryRepository.findById(eventRegistryId)
                .orElseThrow(() -> new EntityNotFoundException("Event was not found in registry"));
        Event event = getActiveEventByName(eventRegistry.getEvent().getName());
        User user = userService.findById(eventRegistry.getUser().getId());
        User userValidator = userDetailsService.getCurrentlyLoggedUser();
        AuthMedium authMedium = authMediumService.getByUserAndAuthType(event.getAuthType(), user);

        log.info("Event request is validated for user with email: {}, using authType: {}, with identificator {}," +
                        " event name: {}, reward is {}, assigned institution: {}, validation made by {}",
                user.getEmail(), authMedium.getAuthType(), authMedium.getIdentificator(),
                event.getName(), event.getReward(), user.getInstitution().getName(), userValidator.getName());

        EventPayload eventPayload = createMessagePayload(event, user, authMedium, user.getInstitution());
        log.info("Message payload is created after validation in order to be sent for reward");

        //TODO: send to rabbit

        eventRegistry.setRewarded(true);
        eventRegistry.setValidatorName(user.getName());
        eventRegistryRepository.save(eventRegistry);
    }

    private void verifyAuthMedium(EventRequest eventRequest, User user, AuthMedium authMedium, Event event) {
        if (!user.getRole().equals(RoleType.CITIZEN) || !(authMedium.getAuthType().equals(event.getAuthType()))) {
            throw new AccessDeniedException("User not allowed to trigger events");
        }
        if (event.getAuthType().equals(AuthType.CARD)) {
            Card card = authMediumService.getCardByCode(eventRequest.getIdentificator());
            if (card.getValidTo().before(new Date())) {
                throw new EventDataException("Card is not valid anymore");
            }
        }
    }

    private EventPayload createMessagePayload(Event event, User user, AuthMedium authMedium, Institution institution) {
        return EventPayload.builder()
                .eventName(event.getName())
                .reward(event.getReward())
                .user(UserConverter.convertUserToUserPayload(user))
                .eventAuthType(event.getAuthType())
                .identificator(authMedium.getIdentificator())
                .institutionWallet(institution.getWalletAdress())
                .institutionName(institution.getName())
                .timestamp(new Date())
                .build();
    }

    private EventRegistry createEventRegistry(Event event, User user, boolean rewarded) {
        return EventRegistry.builder()
                .event(event)
                .user(user)
                .rewarded(rewarded)
                .build();
    }
}
