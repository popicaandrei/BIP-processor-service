package com.processorservice.models.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @Column(name = "code")
    private String code;

    @Column(nullable = false)
    private String number;

    @Column(name ="valid_to", nullable = false)
    private Date validTo;

    @Column(name ="valid_from", nullable = false)
    private Date validFrom;

}
