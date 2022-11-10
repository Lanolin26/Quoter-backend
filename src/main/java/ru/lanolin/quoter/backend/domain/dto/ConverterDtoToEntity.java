package ru.lanolin.quoter.backend.domain.dto;

import ru.lanolin.quoter.backend.domain.IdentificationClass;

public interface ConverterDtoToEntity<Entity extends IdentificationClass<?, ?>>{

	Entity entity();

}
