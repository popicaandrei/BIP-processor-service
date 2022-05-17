package com.processorservice.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventRegistryDto implements Serializable {

    private Integer id;
    private UserDto user;
    private EventDto event;
    private boolean rewarded;
    private Date timestamp;
}
