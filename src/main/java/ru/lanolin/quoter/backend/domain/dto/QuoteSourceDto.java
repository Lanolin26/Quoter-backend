package ru.lanolin.quoter.backend.domain.dto;

import ru.lanolin.quoter.backend.domain.QuoteSource;

import java.io.Serializable;
import java.util.Optional;

/**
 * A DTO for the {@link ru.lanolin.quoter.backend.domain.QuoteSource} entity
 */
public record QuoteSourceDto(
		Integer id,
		String sourceName,
		QuoteSourceTypeDto type
) implements Serializable, ConverterDtoToEntity<QuoteSource> {
	@Override
	public QuoteSource entity() {
		QuoteSource quoteSource = new QuoteSource();
		Optional.ofNullable(id).ifPresent(quoteSource::setId);
		Optional.ofNullable(sourceName).ifPresent(quoteSource::setSourceName);
		Optional.ofNullable(type).map(QuoteSourceTypeDto::entity).ifPresent(quoteSource::setType);
		return quoteSource;
	}
}