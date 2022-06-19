package com.processorservice.services;

import com.processorservice.config.exceptions.EventDataException;
import com.processorservice.messaging.RabbitClient;
import com.processorservice.models.dtos.EventRequest;
import com.processorservice.models.entities.*;
import com.processorservice.models.enums.AuthType;
import com.processorservice.models.enums.RoleType;
import com.processorservice.repositories.EventRegistryRepository;
import com.processorservice.repositories.EventRepository;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class EventServiceTest {

    public static final String IDENTIFICATOR = "identificator";
    public static final String EVENT_NAME = "eventName";
    public static final int EVENT_ID = 1;
    public static final int AUTH_MEDIUM_ID = 1;
    public static final String WALLET_ADDRESS = "walletAddress";
    public static final int EVENT_REGISTRY_ID = 1;
    public static final String USER_NAME = "userName";
    private static final String USER_INSTITUTION_NAME = "userInstitutionName";

    @Mock
    EventRepository eventRepository;
    @Mock
    EventRegistryRepository eventRegistryRepository;
    @Mock
    AuthMediumService authMediumService;
    @Mock
    RabbitClient rabbitClient;
    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    EventService target;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void triggerEventWithCardByCitizen() {
        EventRequest eventRequest = new EventRequest(IDENTIFICATOR, EVENT_NAME);
        Event event = createEvent(EVENT_ID, EVENT_NAME, true, AuthType.CARD, false);
        User user = createUser(RoleType.CITIZEN, WALLET_ADDRESS, USER_NAME);
        AuthMedium authMedium = createAuthMedium(AuthType.CARD, AUTH_MEDIUM_ID, IDENTIFICATOR, user);
        Card card = createValidCard();

        when(eventRepository.findEventByNameAndActive(EVENT_NAME, true)).thenReturn(Optional.ofNullable(event));
        when(authMediumService.getByIdentificator(IDENTIFICATOR)).thenReturn(authMedium);
        when(authMediumService.getCardByCode(IDENTIFICATOR)).thenReturn(card);

        target.triggerEvent(eventRequest);

        verify(rabbitClient).send(any());
        verify(eventRegistryRepository).save(any());
    }

    @Test
    public void triggerEventWithCardByAdmin() {
        EventRequest eventRequest = new EventRequest(IDENTIFICATOR, EVENT_NAME);
        Event event = createEvent(EVENT_ID, EVENT_NAME, true, AuthType.CARD, false);
        User user = createUser(RoleType.ADMIN, WALLET_ADDRESS, USER_NAME);
        AuthMedium authMedium = createAuthMedium(AuthType.CARD, AUTH_MEDIUM_ID, IDENTIFICATOR, user);
        Card card = createValidCard();

        when(eventRepository.findEventByNameAndActive(EVENT_NAME, true)).thenReturn(Optional.ofNullable(event));
        when(authMediumService.getByIdentificator(IDENTIFICATOR)).thenReturn(authMedium);
        when(authMediumService.getCardByCode(IDENTIFICATOR)).thenReturn(card);

        assertThrows(AccessDeniedException.class, () -> target.triggerEvent(eventRequest));

        verifyNoInteractions(rabbitClient);
        verifyNoInteractions(eventRegistryRepository);
    }

    @Test
    public void triggerEventWithNoUserWalletAddress() {
        EventRequest eventRequest = new EventRequest(IDENTIFICATOR, EVENT_NAME);
        Event event = createEvent(EVENT_ID, EVENT_NAME, true, AuthType.CARD, false);
        User user = createUser(RoleType.CITIZEN, null, USER_NAME);
        AuthMedium authMedium = createAuthMedium(AuthType.CARD, AUTH_MEDIUM_ID, IDENTIFICATOR, user);
        Card card = createValidCard();

        when(eventRepository.findEventByNameAndActive(EVENT_NAME, true)).thenReturn(Optional.ofNullable(event));
        when(authMediumService.getByIdentificator(IDENTIFICATOR)).thenReturn(authMedium);
        when(authMediumService.getCardByCode(IDENTIFICATOR)).thenReturn(card);

        assertThrows(EventDataException.class, () -> target.triggerEvent(eventRequest));

        verifyNoInteractions(rabbitClient);
        verifyNoInteractions(eventRegistryRepository);
    }

    @Test
    public void triggerEventWithValidationNeeded() {
        EventRequest eventRequest = new EventRequest(IDENTIFICATOR, EVENT_NAME);
        Event event = createEvent(EVENT_ID, EVENT_NAME, true, AuthType.CARD, true);
        User user = createUser(RoleType.CITIZEN, WALLET_ADDRESS, USER_NAME);
        AuthMedium authMedium = createAuthMedium(AuthType.CARD, AUTH_MEDIUM_ID, IDENTIFICATOR, user);
        Card card = createValidCard();

        when(eventRepository.findEventByNameAndActive(EVENT_NAME, true)).thenReturn(Optional.ofNullable(event));
        when(authMediumService.getByIdentificator(IDENTIFICATOR)).thenReturn(authMedium);
        when(authMediumService.getCardByCode(IDENTIFICATOR)).thenReturn(card);

        target.triggerEvent(eventRequest);

        verifyNoInteractions(rabbitClient);
        verify(eventRegistryRepository).save(any());
    }

    @Test
    void validateEvent() {
        AuthType eventAuthType = AuthType.CARD;
        Event event = createEvent(EVENT_ID, EVENT_NAME, true, eventAuthType, true);
        User userValidator = createUser(RoleType.INSTITUTION, WALLET_ADDRESS, USER_INSTITUTION_NAME);
        User user = createUser(RoleType.CITIZEN, WALLET_ADDRESS, USER_NAME);
        EventRegistry eventRegistry = createEventRegistry(false, event, user);
        AuthMedium authMedium = createAuthMedium(eventAuthType, AUTH_MEDIUM_ID, IDENTIFICATOR, user);

        when(eventRepository.findEventByNameAndActive(EVENT_NAME, true)).thenReturn(Optional.ofNullable(event));
        when(eventRegistryRepository.findById(EVENT_REGISTRY_ID)).thenReturn(Optional.ofNullable(eventRegistry));
        when(userDetailsService.getCurrentlyLoggedUser()).thenReturn(userValidator);
        when(authMediumService.getByUserAndAuthType(eventAuthType, user)).thenReturn(authMedium);

        target.validateEvent(EVENT_REGISTRY_ID);

        verify(rabbitClient).send(any());
        verify(eventRegistryRepository).save(eventRegistry);
        assertTrue(eventRegistry.isRewarded());
        assertEquals(USER_INSTITUTION_NAME, eventRegistry.getValidatorName());
    }

    @Test
    void validateAlreadyRewardedEvent() {
        Event event = createEvent(EVENT_ID, EVENT_NAME, true, AuthType.CARD, true);
        User user = createUser(RoleType.CITIZEN, WALLET_ADDRESS, USER_NAME);
        EventRegistry eventRegistry = createEventRegistry(true, event, user);

        when(eventRegistryRepository.findById(EVENT_REGISTRY_ID)).thenReturn(Optional.ofNullable(eventRegistry));

        assertThrows(EventDataException.class, () -> target.validateEvent(EVENT_REGISTRY_ID));

        verifyNoInteractions(rabbitClient);
    }

    private EventRegistry createEventRegistry(boolean rewarded, Event event, User user) {
        return EventRegistry.builder().id(EVENT_REGISTRY_ID).rewarded(rewarded).event(event).user(user).build();
    }

    private Card createValidCard() {
        return Card.builder().validTo(DateUtil.tomorrow()).build();
    }

    private AuthMedium createAuthMedium(AuthType authType, Integer id, String identificator, User user) {
        return AuthMedium.builder().authType(authType).id(id).identificator(identificator).user(user).build();
    }

    private User createUser(RoleType roleType, String walletAddress, String name) {
        return User.builder()
                .role(roleType)
                .walletAddress(walletAddress)
                .institution(new Institution())
                .name(name)
                .build();
    }

    private Event createEvent(Integer id, String name, Boolean active, AuthType authType, Boolean validationNeeded) {
        return Event.builder()
                .id(id)
                .name(name)
                .active(active)
                .authType(authType)
                .validationNeeded(validationNeeded)
                .build();
    }
}