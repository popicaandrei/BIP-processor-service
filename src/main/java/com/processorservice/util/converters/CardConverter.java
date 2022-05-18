package com.processorservice.util.converters;

import com.processorservice.models.dtos.CardDto;
import com.processorservice.models.entities.Card;

public class CardConverter {

    public static Card convertCardDtoToCard(CardDto dto) {
        return Card.builder()
                .code(dto.getCode())
                .number(dto.getNumber())
                .validTo(dto.getValidTo())
                .validFrom(dto.getValidFrom())
                .build();
    }

    public static CardDto convertCardToCardDto(Card card) {
        return CardDto.builder()
                .code(card.getCode())
                .number(card.getNumber())
                .validTo(card.getValidTo())
                .validFrom(card.getValidFrom())
                .build();
    }
}
