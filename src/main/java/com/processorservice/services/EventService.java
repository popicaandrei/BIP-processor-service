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

    public void addEvent(Event event) {
        log.info("Saving new event with id: {}", event.getId());
        eventRepository.save(event);
    }

    public Event getEventByName(String name) {
        log.info("Getting event with name: {}", name);
        return eventRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    public Event getActiveEventByName(String name) {
        log.info("Getting active event with name: {}", name);
        return eventRepository.findEventByNameAndActive(name, true)
                .orElseThrow(() -> new EntityNotFoundException("Active event not found"));
    }

    public List<Event> getAllEventsByInstitution(Institution institution) {
        return eventRepository.findAllByInstitution(institution);
    }

    @Transactional
    public void triggerEvent(EventRequest eventRequest) {
        Event event = getActiveEventByName(eventRequest.getEventName());
        AuthMedium authMedium = authMediumService.getByIdentificator(eventRequest.getIdentificator());
        User user = authMedium.getUser();

        verifyAuthMedium(eventRequest, user);

        log.info("Event request is triggered for user with email: {}, using authType: {}, with identificator {}," +
                        " event name: {}, reward is {}, assigned institution: {}",
                user.getEmail(), authMedium.getAuthType(), authMedium.getIdentificator(),
                event.getName(), event.getReward(), user.getInstitution().getName());

        EventPayload eventPayload = createMessagePayload(event, user, authMedium, user.getInstitution());
        saveEventRegistry(event, user);
    }

    //TODO: also add trigger with validation -> put method

    private void verifyAuthMedium(EventRequest eventRequest, User user) {
        if (!user.getRole().equals(RoleType.CITIZEN)) {
            throw new AccessDeniedException("User not allowed to trigger events");
        }
        if (eventRequest.getAuthType().equals(AuthType.CARD)) {
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
                .authType(authMedium.getAuthType())
                .identificator(authMedium.getIdentificator())
                .institutionWallet(institution.getWalletAdress())
                .institutionName(institution.getName())
                .timestamp(new Date())
                .build();
    }

    private void saveEventRegistry(Event event, User user) {
        EventRegistry eventRegistry = EventRegistry.builder()
                .event(event)
                .user(user)
                .rewarded(true)
                .build();
        eventRegistryRepository.save(eventRegistry);
    }
}
