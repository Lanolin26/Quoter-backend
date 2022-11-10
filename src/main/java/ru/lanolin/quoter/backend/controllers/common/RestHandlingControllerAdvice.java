package ru.lanolin.quoter.backend.controllers.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;

@ControllerAdvice
public class RestHandlingControllerAdvice {

	@ExceptionHandler({ IncorrectField.class })
	public ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
		return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

}
