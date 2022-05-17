package com.processorservice.repositories;

import com.processorservice.models.entities.EventRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistryRepository extends JpaRepository<EventRegistry, Integer> {
}