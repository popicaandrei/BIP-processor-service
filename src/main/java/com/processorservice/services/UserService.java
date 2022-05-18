package com.processorservice.services;

import com.processorservice.config.exceptions.UserAlreadyExistException;
import com.processorservice.config.exceptions.UserNotFoundException;
import com.processorservice.models.dtos.RegisterRequest;
import com.processorservice.models.entities.Institution;
import com.processorservice.models.entities.User;
import com.processorservice.repositories.UserRepository;
import com.processorservice.util.converters.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        user.setInstitution(institutionService.getInstitution(registerRequest.getInstitutionId()));
        log.info("User newly registered with mail: {}", user.getEmail());
        userRepository.save(user);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public List<User> getAllUsersByInstitution(Integer institutionId) {
        Institution institution = institutionService.getInstitution(institutionId);
        return userRepository.findAllByInstitution(institution);
    }

    private boolean checkUserIfUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
