package com.processorservice.models.messaging;

import com.processorservice.models.dtos.UserPayload;
import com.processorservice.models.enums.AuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Builder
public class EventPayload {

    private String eventName;
    private Double reward;
    private UserPayload user;
    private String institutionWallet;
    private String institutionName;
    private AuthType eventAuthType;
    private String identificator;
    private Date timestamp;
}