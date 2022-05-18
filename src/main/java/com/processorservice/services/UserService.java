package com.processorservice.services;

import com.processorservice.config.exceptions.UserAlreadyExistException;
import com.processorservice.config.exceptions.UserNotFoundException;
import com.processorservice.models.dtos.RegisterRequest;
import com.processorservice.models.entities.User;
import com.processorservice.repositories.UserRepository;
import com.processorservice.util.converters.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    InstitutionService institutionService;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public void register(RegisterRequest registerRequest) {
        User user = UserConverter.convertRegisterRequestToEntity(registerRequest);
        if (checkUserIfUserExists(user.getEmail())) {
            throw new UserAlreadyExistException("User already registered for this user mail");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setInstitution(institutionService.getInstitution(registerRequest.institutionId()));
        log.info("User newly registered with mail: {}", user.getEmail());
        userRepository.save(user);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User getUserById(Integer id) {
        User user = findById(id);
        User userLoggedIn = userDetailsService.getCurrentlyLoggedUser().get();
        if (userLoggedIn.getEmail().equals(user.getEmail()))
            return user;
        else throw new AccessDeniedException("Resource access is forbidden.");
    }

    private boolean checkUserIfUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
