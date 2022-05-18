package com.processorservice.controllers;

import com.processorservice.models.dtos.RegisterRequest;
import com.processorservice.models.dtos.UserDto;
import com.processorservice.services.UserService;
import com.processorservice.util.converters.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());
        userService.register(registerRequest);
    }

    @GetMapping("users/{userId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Integer userId) {
        log.info("Retrieving user with id: {}", userId);
        return UserConverter.convertEntityToDto(userService.getUserById(userId));
    }

}
