package com.processorservice.controllers;

import com.processorservice.models.dtos.InstitutionDto;
import com.processorservice.services.InstitutionService;
import com.processorservice.util.converters.InstitutionCoverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/institutions")
@Slf4j
public class InstitutionController {

    @Autowired
    private InstitutionService institutionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_INSTITUTION')")
    public void addInstitution(@RequestBody InstitutionDto institutionDto) {
        log.info("Registering new institution with name: {}", institutionDto.getName());
        institutionService.addInstitution(InstitutionCoverter.convertDtoToEntity(institutionDto));
    }

}
