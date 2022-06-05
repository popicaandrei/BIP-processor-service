package com.processorservice.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.processorservice.models.enums.AuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserActivityDto {

    private Integer id;
    private String event;
    private String timestamp;
    private Double reward;
    private boolean isRewarded;
    private AuthType authType;
}