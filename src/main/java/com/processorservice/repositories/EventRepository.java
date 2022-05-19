package com.processorservice.repositories;

import com.processorservice.models.entities.Event;
import com.processorservice.models.entities.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findAllByInstitution(Institution institution);

    Optional<Event> findById(Integer id);

    Optional<Event> findByName(String name);

    Optional <Event> findEventByNameAndActive(String name, boolean active);
}