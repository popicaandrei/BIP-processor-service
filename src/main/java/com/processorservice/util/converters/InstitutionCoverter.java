package com.processorservice.util.converters;

import com.processorservice.models.dtos.InstitutionDto;
import com.processorservice.models.entities.Institution;

public class InstitutionCoverter {

    public static Institution convertDtoToEntity(InstitutionDto dto) {
        return Institution.builder()
                .name(dto.getName())
                .walletAdress(dto.getWalletAdress())
                .build();
    }

    public static InstitutionDto convertDtoToEntity(Institution entity) {
        return InstitutionDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .walletAdress(entity.getWalletAdress())
                .build();
    }
}
