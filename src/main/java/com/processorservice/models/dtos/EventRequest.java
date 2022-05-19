package com.processorservice.models.dtos;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class EventRequest {

    @NotNull
    private String identificator;
    @NotNull
    private String eventName;
}
