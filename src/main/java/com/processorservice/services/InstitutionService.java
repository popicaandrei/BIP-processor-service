package com.processorservice.services;

import com.processorservice.models.entities.Institution;
import com.processorservice.repositories.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class InstitutionService {

    @Autowired
    InstitutionRepository institutionRepository;

    public Institution getInstitution(Integer id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Institution not found"));
    }

    public void addInstitution(Institution institution){
        institutionRepository.save(institution);
    }
}
