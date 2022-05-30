package com.processorservice.repositories;

import com.processorservice.models.entities.Event;
import com.processorservice.models.entities.EventRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRegistryRepository extends JpaRepository<EventRegistry, Integer> {

   Optional<EventRegistry> findByRewardedAndEvent(boolean rewarded, Event event);
}