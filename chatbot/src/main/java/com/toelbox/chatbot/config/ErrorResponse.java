package com.toelbox.chatbot.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrorResponse {

	@JsonProperty("status")
	private int status;

	@JsonProperty("error")
	private String error;

	@JsonProperty("message")
	private String message;

	@JsonProperty("path")
	private String path;

	@JsonProperty("timestamp")
	private LocalDateTime timestamp;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("errors")
	private Map<String, String> errors;
}
