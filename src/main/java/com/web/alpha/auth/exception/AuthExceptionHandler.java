package com.web.alpha.auth.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.web.alpha.auth")
public class AuthExceptionHandler {

	@ExceptionHandler(AuthApiException.class)
	public ResponseEntity<AuthErrorResponse> handleAuthApiException(
			AuthApiException exception,
			HttpServletRequest request
	) {
		return buildResponse(
				exception.getStatus(),
				exception.getCode(),
				exception.getMessage(),
				request,
				Map.of()
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<AuthErrorResponse> handleValidationException(
			MethodArgumentNotValidException exception,
			HttpServletRequest request
	) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			String message = fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage();
			fieldErrors.putIfAbsent(fieldError.getField(), message);
		}

		return buildResponse(
				HttpStatus.BAD_REQUEST,
				"AUTH_VALIDATION_FAILED",
				"Request validation failed",
				request,
				fieldErrors
		);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<AuthErrorResponse> handleUnreadableMessage(
			HttpMessageNotReadableException exception,
			HttpServletRequest request
	) {
		return buildResponse(
				HttpStatus.BAD_REQUEST,
				"AUTH_MALFORMED_JSON",
				findJsonMessage(exception),
				request,
				Map.of()
		);
	}

	private ResponseEntity<AuthErrorResponse> buildResponse(
			HttpStatus status,
			String code,
			String message,
			HttpServletRequest request,
			Map<String, String> fieldErrors
	) {
		AuthErrorResponse response = new AuthErrorResponse(
				Instant.now().toString(),
				status.value(),
				status.getReasonPhrase(),
				code,
				message,
				request.getRequestURI(),
				fieldErrors
		);
		return ResponseEntity.status(status).body(response);
	}

	private String findJsonMessage(HttpMessageNotReadableException exception) {
		Throwable cause = exception;
		while (cause != null) {
			if (cause instanceof JsonMappingException jsonMappingException) {
				return jsonMappingException.getOriginalMessage();
			}
			cause = cause.getCause();
		}
		return "Request body contains malformed JSON";
	}
}
