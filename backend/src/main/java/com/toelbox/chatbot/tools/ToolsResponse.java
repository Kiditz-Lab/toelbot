package com.toelbox.chatbot.tools;

import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.UUID;


record ToolsResponse(
		UUID id,
		String name,
		String imageUrl,
		String description,
		String shortDescription,
		List<McpSchema.Tool> tools,
		String command,
		List<String> args,
		Map<String, String> env
) {
}
