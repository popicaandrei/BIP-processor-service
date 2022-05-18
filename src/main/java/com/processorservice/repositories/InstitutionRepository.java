package com.processorservice.repositories;

import com.processorservice.models.entities.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Integer> {

    Optional<Institution> findById(Integer id);

    Optional<Institution> findByName(String name);
}