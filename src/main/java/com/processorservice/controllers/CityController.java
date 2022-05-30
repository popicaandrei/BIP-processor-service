package com.processorservice.controllers;

import com.processorservice.models.dtos.EventDto;
import com.processorservice.models.dtos.EventRegistryDto;
import com.processorservice.models.dtos.InstitutionDto;
import com.processorservice.models.dtos.UserDto;
import com.processorservice.services.EventService;
import com.processorservice.services.InstitutionService;
import com.processorservice.services.UserService;
import com.processorservice.util.converters.EventConverter;
import com.processorservice.util.converters.InstitutionCoverter;
import com.processorservice.util.converters.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/institutions")
@PreAuthorize("hasRole('ROLE_INSTITUTION')")
@Slf4j
public class CityController {

    @Autowired
    private InstitutionService institutionService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addInstitution(@RequestBody InstitutionDto institutionDto) {
        log.info("Registering new institution with name: {}", institutionDto.getName());
        institutionService.addInstitution(InstitutionCoverter.convertDtoToEntity(institutionDto));
    }

    @GetMapping("/{institutionId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsersForInstitution(@PathVariable Integer institutionId) {
        log.info("Retrieving all the users for institution with id: {}", institutionId);
        return userService.getAllUsersByInstitution(institutionId).stream()
                .map(UserConverter::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEvent(@RequestBody EventDto eventDto) {
        log.info("Adding new event with name: {}", eventDto.getName());
        eventService.addEvent(EventConverter.convertDtoToEntity(eventDto));
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getAllEventCreatedByInstitution() {
        log.info("Getting all the events for current institution.");
        return eventService.getAllEventsByInstitution().stream()
                .map(EventConverter::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/validate")
    @ResponseStatus(HttpStatus.OK)
    public List<EventRegistryDto> getAllEventsNotValidated() {
        log.info("Getting all the events that needs validation.");
        return eventService.getAllEventsByInstitutionNotValidated();
    }

    @PutMapping("/events/validate/{eventRegistryId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void validateEvent(@PathVariable Integer eventRegistryId) {
        log.info("Validating event with id: {}", eventRegistryId);
        eventService.validateEvent(eventRegistryId);
    }

}