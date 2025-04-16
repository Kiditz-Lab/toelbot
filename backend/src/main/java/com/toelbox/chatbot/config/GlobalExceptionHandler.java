package com.toelbox.chatbot.config;

import com.toelbox.chatbot.core.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> handleValidationException(
			MethodArgumentNotValidException ex,
			WebRequest request) {

		Map<String, String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.toMap(
						FieldError::getField,
						field -> Objects.requireNonNull(field.getDefaultMessage()),
						(existing, replacement) -> existing // in case of duplicate keys
				));

		ErrorResponse response = ErrorResponse.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.error("Bad Request")
				.message("Validation failed")
				.path(request.getDescription(false).replace("uri=", ""))
				.timestamp(LocalDateTime.now())
				.errors(errors)
				.build();

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(
			NotFoundException ex,
			WebRequest request) {

		ErrorResponse response = ErrorResponse.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.error("Not Found")
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(
			NoResourceFoundException ex,
			WebRequest request) {

		ErrorResponse response = ErrorResponse.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.error("Not Found")
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception ex,
			WebRequest request) {

		// Log the full stack trace for internal debugging
		log.error("Unhandled exception occurred", ex);

		ErrorResponse response = ErrorResponse.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error("Internal Server Error")
				.message("Something went wrong. Please contact support.") // avoid exposing internal error details
				.path(request.getDescription(false).replace("uri=", ""))
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public ResponseEntity<ErrorResponse> handleMediaTypeNotAcceptable(
			HttpMediaTypeNotAcceptableException ex,
			HttpServletRequest request) {

		log.warn("HttpMediaTypeNotAcceptableException at path {}: {}", request.getRequestURI(), ex.getMessage());

		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.NOT_ACCEPTABLE.value())
				.error("Not Acceptable")
				.message("Requested media type is not supported")
				.path(request.getRequestURI())
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
	}


	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex,
			WebRequest request, HttpServletRequest servletRequest) {
		log.warn("Data Integrity: {} {}", servletRequest.getMethod(), servletRequest.getRequestURI());
		// Check for a unique constraint violation and respond accordingly
		if (ex.getMessage().contains("duplicate key value violates unique constraint")) {
			ErrorResponse response = ErrorResponse.builder()
					.status(HttpStatus.BAD_REQUEST.value())
					.error("Bad Request")
					.message("A unique constraint violation occurred. Please ensure the data is unique.")
					.path(request.getDescription(false).replace("uri=", ""))
					.timestamp(LocalDateTime.now())
					.build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// For other data integrity violations
		ErrorResponse response = ErrorResponse.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error("Internal Server Error")
				.message("An error occurred while processing your request.")
				.path(request.getDescription(false).replace("uri=", ""))
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(NoResourceFoundException ex, HttpServletRequest request) {
		log.warn("Not Found: {} {}", request.getMethod(), request.getRequestURI());

		ErrorResponse error = ErrorResponse.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getRequestURI())
				.timestamp(LocalDateTime.now())
				.build();

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

}
