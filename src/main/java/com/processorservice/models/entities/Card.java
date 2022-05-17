package com.processorservice.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
public class Card {

    @Id
    @Column(name = "code")
    private String code;

    @Column(nullable = false)
    private String number;

    @Column(name ="valid_to", nullable = false)
    private String validTo;

    @Column(name ="valid_from", nullable = false)
    private String validFrom;

}
