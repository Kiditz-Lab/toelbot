package com.toelbox.chatbot.tools;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;


record ToolsCommand(
		@NotEmpty
		String name,
		@NotBlank
		String imageUrl,
		@NotBlank
		String description,
		@NotBlank
		@Size(max = 150)
		String shortDescription,
		@NotBlank
		String command,
		@NotEmpty
		List<String> args,
		Map<String, String> env
) {
}
