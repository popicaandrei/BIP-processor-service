package com.processorservice.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventRegistryDto {

    private Integer id;
    private UserDto user;
    private EventDto event;
    private boolean rewarded;
    private Date timestamp;
}
