package com.processorservice.controllers;

import com.processorservice.models.dtos.RegisterRequest;
import com.processorservice.models.dtos.UserDto;
import com.processorservice.services.UserDetailsService;
import com.processorservice.services.UserService;
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
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    UserDetailsService userDetailsService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());
        userService.register(registerRequest);
    }

    @GetMapping("/users")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getLoggedInUser() {
        log.info("Retrieving currently logged in user");
        return UserConverter.convertEntityToDto(userDetailsService.getCurrentlyLoggedUser());
    }

    @GetMapping("/institutions/{institutionId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_INSTITUTION')")
    public List<UserDto> getAllUsersForInstitution(@PathVariable Integer institutionId) {
        log.info("Retrieving all the users for institution with id: {}", institutionId);
        return userService.getAllUsersByInstitution(institutionId).stream()
                .map(UserConverter::convertEntityToDto)
                .collect(Collectors.toList());
    }

}
