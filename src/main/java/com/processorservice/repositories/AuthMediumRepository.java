package com.processorservice.repositories;

import com.processorservice.models.entities.AuthMedium;
import com.processorservice.models.entities.User;
import com.processorservice.models.enums.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthMediumRepository extends JpaRepository<AuthMedium, Integer> {

    List<AuthMedium> getAuthMediumByAuthTypeAndUser(AuthType authType, User user);
}