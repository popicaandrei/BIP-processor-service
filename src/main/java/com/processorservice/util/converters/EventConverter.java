package com.processorservice.util.converters;

import com.processorservice.models.dtos.EventDto;
import com.processorservice.models.entities.Event;

public class EventConverter {

    public static Event convertDtoToEntity(EventDto dto) {
        return Event.builder()
                .name(dto.getName())
                .validationNeeded(dto.isValidationNeeded())
                .reward(dto.getReward())
                .active(dto.isActive())
                .authType(dto.getAuthType())
                .build();
    }

    public static EventDto convertEntityToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .validationNeeded(event.isValidationNeeded())
                .reward(event.getReward())
                .active(event.isActive())
                .authType(event.getAuthType())
                .build();
    }
}
