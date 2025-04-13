package com.toelbox.chatbot.tools;

import io.modelcontextprotocol.spec.McpSchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tools")
@Tag(name = "Tools")
@RequiredArgsConstructor
class ToolsCommandController {
	private final ToolsCommandService commandService;

	@PostMapping
	Tools createTool(@RequestBody @Valid ToolsCommand command) {
		return commandService.createTool(command);
	}

	@PutMapping("/{id}")
	void updateTool(@PathVariable UUID id, @RequestBody @Valid ToolsCommand command) {
		commandService.updateTool(id, command);
	}

	@PostMapping("/test")
	McpSchema.CallToolResult tesTool(@RequestBody @Valid TestToolCommand command) {
		return commandService.testTool(command);
	}

	@DeleteMapping("/{id}")
	void deleteTool(@PathVariable UUID id) {
		commandService.deleteTool(id);
	}

}
