package com.toelbox.chatbot.agent;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Tag(name = "Agents")
class AgentCommandController {
	private final AgentCommandService service;
	private final McpToolsConnectionService connectionService;

	@PostMapping
	Agent createBot(@RequestBody AgentCommand command) {
		return service.createBot(command);
	}

	@PatchMapping("/{id}/config")
	Agent addConfig(@PathVariable UUID id, @RequestBody ConfigCommand command) {
		return service.addConfig(id, command);
	}

	@PatchMapping("/{id}/name")
	Agent updateName(@PathVariable UUID id, @RequestBody AgentCommand command) {
		return service.updateName(id, command);
	}

	@DeleteMapping("/{id}")
	void deleteAgent(@PathVariable UUID id) {
		service.deleteAgent(id);
	}
}
