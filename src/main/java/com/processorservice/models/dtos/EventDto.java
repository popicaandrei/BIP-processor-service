package com.processorservice.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto implements Serializable {

    private Integer id;
    private String name;
    private Integer reward;
    private boolean active;
    private boolean validationNeeded;
    private Date timestamp;
}
