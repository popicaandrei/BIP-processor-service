package com.processorservice.services;

import com.processorservice.models.entities.Event;
import com.processorservice.repositories.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventService {

    @Autowired
    EventRepository eventRepository;

    public void addEvent(Event event){
        log.info("Saving new event with id: {}", event.getId());
        eventRepository.save(event);
    }
}
