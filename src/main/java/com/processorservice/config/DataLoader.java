package com.processorservice.config;

import com.processorservice.models.entities.Address;
import com.processorservice.models.entities.Institution;
import com.processorservice.models.entities.User;
import com.processorservice.repositories.InstitutionRepository;
import com.processorservice.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static com.processorservice.models.enums.RoleType.*;

@Configuration
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createInitialData();
    }

    public void createInitialData() {
        if (userRepository.count() == 0 && institutionRepository.count() == 0) {
            Institution institution = new Institution("Cluj-Napoca", "erd593846503");
            institution = institutionRepository.save(institution);

            User citizenUser = new User("Andrei Popica", "popica.andreivlad@gmail.com", passwordEncoder.encode("pass"),
                    CITIZEN, "0771721420", "erd423345", new Address("M.Eminescu", "534", "453244"), institution);
            User adminUser = new User("Admin", "admin@gmail.com", passwordEncoder.encode("pass"),
                    ADMIN, "0755217136", null, null, institution);
            User institutionUser = new User("Cluj-Napoca", "institution@gmail.com", passwordEncoder.encode("pass"),
                    INSTITUTION, "0755217136", null, null, institution);
            List<User> users = Arrays.asList(citizenUser, adminUser, institutionUser);

            log.info("Initial data created continue to saving");
            userRepository.saveAll(users);
        }
    }
}
