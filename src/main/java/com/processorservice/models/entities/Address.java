package com.processorservice.models.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String street;
    private String number;
    private String zipCode;
}
