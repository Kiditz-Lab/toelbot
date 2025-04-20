package com.toelbox.chatbot.agent;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mcp")
@RequiredArgsConstructor
@Tag(name = "Mcp Server")
class McpServerController {

	private final McpToolsConnectionService connectionService;

	@GetMapping("/agent/{agentId}/tools/{toolsId}")
	public McpServer findByAgentAndTool(@PathVariable UUID agentId, @PathVariable UUID toolsId) {
		return connectionService.findByAgentIdAndToolsId(agentId, toolsId);
	}

	@PostMapping("/agent/{agentId}/tools")
	public void connectTools(@PathVariable UUID agentId, @RequestBody ToolsConnectorCommand command) {
		connectionService.connectTools(agentId, command);
	}

	@PutMapping("/agent/{agentId}/tools/{toolsId}/env")
	public void updateEnv(
			@PathVariable UUID agentId,
			@PathVariable UUID toolsId,
			@RequestBody McpServerUpdate update
	) {
		connectionService.updateEnv(agentId, toolsId, update);
	}

	@DeleteMapping("/agent/{agentId}/tools/{toolsId}")
	public void deleteByAgentAndTool(@PathVariable UUID agentId, @PathVariable UUID toolsId) {
		connectionService.deleteByAgentAndTool(agentId, toolsId);
	}
}
