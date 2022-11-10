package ru.lanolin.quoter.backend.exceptions.domain;

import java.util.function.Predicate;

public record CheckArgs<E>(
		Predicate<E> func,
		String field,
		String value,
		String message
) {
	public IncorrectField createException(Class<E> clazz) {
		return new IncorrectField(clazz, field, value, message);
	}
}
