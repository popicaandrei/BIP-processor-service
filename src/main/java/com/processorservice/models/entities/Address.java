package com.processorservice.models.entities;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Address {

    @NotNull
    private String street;
    @NotNull
    private String number;
    @NotNull
    private String zipCode;
}
