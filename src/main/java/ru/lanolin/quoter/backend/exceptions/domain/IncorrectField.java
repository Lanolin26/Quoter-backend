package ru.lanolin.quoter.backend.exceptions.domain;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Incorrect value on field")
public class IncorrectField extends RuntimeException {
	private static final String format = "%s -> %s = %s: %s";

	public IncorrectField() {
		super("Incorrect field. Check right value");
	}

	public IncorrectField(String message) {
		super(message);
	}

	public IncorrectField(Class<?> clazz, String field, String value, String message) {
		super(String.format(format, clazz.getCanonicalName(), field, value, message));
	}

	public IncorrectField(BindException bindException) {
		super(bindException);
	}

}
