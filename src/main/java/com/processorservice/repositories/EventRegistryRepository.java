package com.processorservice.repositories;

import com.processorservice.models.entities.Event;
import com.processorservice.models.entities.EventRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRegistryRepository extends JpaRepository<EventRegistry, Integer> {

   List<EventRegistry> findAllByRewardedAndEvent(boolean rewarded, Event event);
}