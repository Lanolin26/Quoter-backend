package ru.lanolin.quoter.backend.domain.dto;

public record QuoteEntityIdsInfoDto(
        Integer id,
        String text,
        Integer sourceId,
        Integer authorId
) {
}
