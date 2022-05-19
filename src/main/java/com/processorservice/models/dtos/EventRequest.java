package com.processorservice.models.dtos;

import com.processorservice.models.enums.AuthType;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class EventRequest {

    @NotNull
    private AuthType authType;
    @NotNull
    private String identificator;
    @NotNull
    private String eventName;
}
