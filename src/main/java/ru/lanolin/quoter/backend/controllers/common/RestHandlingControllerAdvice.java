package ru.lanolin.quoter.backend.controllers.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.security.JwtTokenRepository;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RestHandlingControllerAdvice {

	private final JwtTokenRepository tokenRepository;

	@ExceptionHandler({IncorrectField.class})
	public ResponseEntity<String> handleAccessDeniedException(Exception ex) {
		return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = {
			AuthenticationException.class,
			MissingCsrfTokenException.class,
			InvalidCsrfTokenException.class,
			SessionAuthenticationException.class
	})
	public ResponseEntity<String> handleAuthenticationException(RuntimeException ex, HttpServletResponse response) {
		this.tokenRepository.clearToken(response);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return new ResponseEntity<>("error.authorization: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = {AccessDeniedException.class})
	public ResponseEntity<String> handleAccessDenyException(RuntimeException ex, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

}
