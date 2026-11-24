package com.sprint.bank.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.sprint.bank.dto.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, WebRequest request) {
		Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(
						error -> error.getField(),
						error -> error.getDefaultMessage() == null ? "Invalid value." : error.getDefaultMessage(),
						(first, ignored) -> first,
						LinkedHashMap::new));
		return response(HttpStatus.BAD_REQUEST, "Validation failed.", request, errors);
	}

	@ExceptionHandler(EmailAlreadyRegisteredException.class)
	public ResponseEntity<ApiError> handleDuplicateEmail(EmailAlreadyRegisteredException exception, WebRequest request) {
		return response(HttpStatus.CONFLICT, exception.getMessage(), request, Map.of());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleDuplicateProfile(DataIntegrityViolationException exception, WebRequest request) {
		return response(HttpStatus.CONFLICT, "An account with the supplied details already exists.", request, Map.of());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException exception, WebRequest request) {
		return response(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.", request, Map.of());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleInvalidArgument(IllegalArgumentException exception, WebRequest request) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request, Map.of());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException exception, WebRequest request) {
		return response(HttpStatus.BAD_REQUEST, "Request body is missing or malformed.", request, Map.of());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException exception, WebRequest request) {
		return response(HttpStatus.METHOD_NOT_ALLOWED, "The HTTP method is not supported for this endpoint.", request, Map.of());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(NoResourceFoundException exception, WebRequest request) {
		return response(HttpStatus.NOT_FOUND, "The requested endpoint was not found.", request, Map.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception exception, WebRequest request) {
		logger.error("Unexpected request failure", exception);
		return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request, Map.of());
	}

	private ResponseEntity<ApiError> response(
			HttpStatus status,
			String message,
			WebRequest request,
			Map<String, String> validationErrors) {
		String path = ((ServletWebRequest) request).getRequest().getRequestURI();
		ApiError error = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path, validationErrors);
		return ResponseEntity.status(status).body(error);
	}
}
