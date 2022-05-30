package com.processorservice.services;

import com.processorservice.config.exceptions.EventDataException;
import com.processorservice.messaging.RabbitClient;
import com.processorservice.models.dtos.EventRegistryDto;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Autowired
    RabbitClient rabbitClient;

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

    public List<EventRegistryDto> getAllEventsByInstitutionNotValidated() {
        Institution institution = institutionService.getInstitutionByRepresentative();
        List<Event> events = eventRepository.findAllByInstitution(institution);

        List<EventRegistry> registries = new ArrayList<>();
        events.stream().forEach((event) ->
                        registries.addAll(eventRegistryRepository.findAllByRewardedAndEvent(false, event)));

        return registries.stream()
                .filter((Objects::nonNull))
                .map(eventRegistry -> createEventRegistryDto(eventRegistry.getEvent(), eventRegistry.getUser(), eventRegistry))
                .collect(Collectors.toList());
    }

    @Transactional
    public void triggerEvent(EventRequest eventRequest) {
        EventRegistry eventRegistry = null;
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
            EventPayload eventPayload = createMessagePayload(eventRegistry, event, user, authMedium, user.getInstitution());
            log.info("Message payload is created in order to be sent for reward");
            eventRegistry = createEventRegistry(event, user, true);
            rabbitClient.send(eventPayload);
        }
        eventRegistryRepository.save(eventRegistry);
    }

    public void validateMultipleEvents(List<Integer> eventRegistryIds) {
        eventRegistryIds.forEach((eventRegistryId) -> {
            try {
                validateEvent(eventRegistryId);
            } catch (Exception ex) {
                log.error("Error appeared validating event in registry {}", eventRegistryId);
            }
        });
    }

    @Transactional
    public void validateEvent(Integer eventRegistryId) {
        EventRegistry eventRegistry = eventRegistryRepository.findById(eventRegistryId)
                .orElseThrow(() -> new EntityNotFoundException("Event was not found in registry"));

        if (eventRegistry.isRewarded()) {
            throw new EventDataException("Event is already rewarded");
        }

        Event event = getActiveEventByName(eventRegistry.getEvent().getName());
        User user = userService.findById(eventRegistry.getUser().getId());
        User userValidator = userDetailsService.getCurrentlyLoggedUser();
        AuthMedium authMedium = authMediumService.getByUserAndAuthType(event.getAuthType(), user);

        log.info("Event request is validated for user with email: {}, using authType: {}, with identificator {}," +
                        " event name: {}, reward is {}, assigned institution: {}, validation made by {}",
                user.getEmail(), authMedium.getAuthType(), authMedium.getIdentificator(),
                event.getName(), event.getReward(), user.getInstitution().getName(), userValidator.getName());

        EventPayload eventPayload = createMessagePayload(eventRegistry, event, user, authMedium, user.getInstitution());
        log.info("Message payload is created after validation in order to be sent for reward");

        rabbitClient.send(eventPayload);

        eventRegistry.setRewarded(true);
        eventRegistry.setValidatorName(userValidator.getName());
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
        if (user.getWalletAddress() == null) throw new EventDataException("Wallet address should not be null");
    }

    private EventPayload createMessagePayload(EventRegistry eventRegistry, Event event, User user, AuthMedium authMedium, Institution institution) {
        return EventPayload.builder()
                .eventName(event.getName())
                .reward(event.getReward())
                .user(UserConverter.convertUserToUserPayload(user))
                .eventAuthType(event.getAuthType())
                .identificator(authMedium.getIdentificator())
                .institutionWallet(institution.getWalletAdress())
                .institutionName(institution.getName())
                .timestamp(eventRegistry.getTimestamp())
                .build();
    }

    private EventRegistry createEventRegistry(Event event, User user, boolean rewarded) {
        return EventRegistry.builder()
                .event(event)
                .user(user)
                .rewarded(rewarded)
                .build();
    }

    private EventRegistryDto createEventRegistryDto(Event event, User user, EventRegistry eventRegistry) {
        return EventRegistryDto.builder()
                .id(eventRegistry.getId())
                .userEmail(user.getEmail())
                .userName(user.getName())
                .event(event.getName())
                .authType(event.getAuthType())
                .timestamp(dateFormatter(eventRegistry.getTimestamp()))
                .reward(event.getReward())
                .build();
    }

    private String dateFormatter(Date date) {
        DateFormat inputFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return inputFormatter.format(date);
    }
}
