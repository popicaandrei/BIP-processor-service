package com.processorservice.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.processorservice.models.entities.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPayload {

    private String name;
    private String email;
    private String walletAddress;
    private String phoneNumber;
    private Address cityAddress;
}