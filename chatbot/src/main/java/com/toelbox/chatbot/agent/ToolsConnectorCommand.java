package com.toelbox.chatbot.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

record ToolsConnectorCommand(
		UUID toolsId,
		@NotBlank
		String serverName,
		@NotBlank
		String command,
		@NotEmpty
		List<String> args,
		Map<String, String> env
) {
}
