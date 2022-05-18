package com.processorservice.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDto implements Serializable {

    private Integer id;
    private String code;
    private String number;
    private String validTo;
    private String validFrom;
}
