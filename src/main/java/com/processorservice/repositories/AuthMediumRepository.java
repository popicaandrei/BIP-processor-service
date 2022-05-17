package com.processorservice.repositories;

import com.processorservice.models.entities.AuthMedium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthMediumRepository extends JpaRepository<AuthMedium, Integer> {
}