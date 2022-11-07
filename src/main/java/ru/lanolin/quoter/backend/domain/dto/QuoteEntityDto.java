package ru.lanolin.quoter.backend.domain.dto;

import ru.lanolin.quoter.backend.domain.QuoteEntity;

import java.io.Serializable;
import java.util.Optional;

/**
 * A DTO for the {@link ru.lanolin.quoter.backend.domain.QuoteEntity} entity
 */
public record QuoteEntityDto(
		Integer id,
		String text,
		UserEntityDto author,
		QuoteSourceDto source
) implements Serializable, ConverterDtoToEntity<QuoteEntity> {
	@Override
	public QuoteEntity entity() {
		QuoteEntity quoteEntity = new QuoteEntity();
		Optional.ofNullable(id).ifPresent(quoteEntity::setId);
		Optional.ofNullable(text).ifPresent(quoteEntity::setText);
		Optional.ofNullable(author).map(UserEntityDto::entity).ifPresent(quoteEntity::setAuthor);
		Optional.ofNullable(source).map(QuoteSourceDto::entity).ifPresent(quoteEntity::setSource);
		return quoteEntity;
	}
}