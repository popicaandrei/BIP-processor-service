package com.processorservice.services;

import com.processorservice.models.entities.Event;
import com.processorservice.models.entities.Institution;
import com.processorservice.models.entities.User;
import com.processorservice.repositories.InstitutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Slf4j
public class InstitutionService {

    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    EventService eventService;
    @Autowired
    UserDetailsService userDetailsService;

    public Institution getInstitution(Integer id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Institution not found"));
    }

    public void addInstitution(Institution institution) {
        institutionRepository.save(institution);
    }

    public void addEvent(Event event) {
        Institution institution = getInstitutionByRepresentative();
        event.setInstitution(institution);
        log.info("Adding new event with id: {}, for institution with id: {}", event.getId(), institution.getId());
        eventService.addEvent(event);
    }

    public List<Event> getAllEventsByInstitution() {
        return eventService.getAllEventsByInstitution(getInstitutionByRepresentative());
    }

    private Institution getInstitutionByRepresentative() {
        User user = userDetailsService.getCurrentlyLoggedUser();
        log.info("Getting information regarding institution using the user with mail: {}", user.getEmail());
        return getInstitution(user.getInstitution().getId());
    }
}
