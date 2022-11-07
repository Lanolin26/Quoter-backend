package ru.lanolin.quoter.backend.domain.dto;

import ru.lanolin.quoter.backend.domain.QuoteSourceType;

import java.io.Serializable;
import java.util.Optional;

/**
 * A DTO for the {@link ru.lanolin.quoter.backend.domain.QuoteSourceType} entity
 */
public record QuoteSourceTypeDto(
		Integer id,
		String type
) implements Serializable, ConverterDtoToEntity<QuoteSourceType> {
	@Override
	public QuoteSourceType entity() {
		QuoteSourceType quoteSourceType = new QuoteSourceType();
		Optional.ofNullable(id).ifPresent(quoteSourceType::setId);
		Optional.ofNullable(type).ifPresent(quoteSourceType::setType);
		return quoteSourceType;
	}
}