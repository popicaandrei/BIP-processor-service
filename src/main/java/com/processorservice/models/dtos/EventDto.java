package com.processorservice.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.processorservice.models.enums.AuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {

    private Integer id;
    private String name;
    private Double reward;
    private boolean active;
    private boolean validationNeeded;
    private AuthType authType;
}
