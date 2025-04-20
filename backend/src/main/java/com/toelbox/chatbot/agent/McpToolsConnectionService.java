package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class McpToolsConnectionService {
	private final McpServerRepository mcpServerRepository;
	private final AgentRepository agentRepository;
	private final AgentChatService chatService;

	void connectTools(UUID agentId, ToolsConnectorCommand command) {
		var server = McpServer.builder()
				.toolsId(command.toolsId())
				.serverName(command.serverName())
				.command(command.command())
				.args(command.args())
				.env(command.env())
				.usedTools(command.usedTools())
				.agentId(agentId)
				.build();
		Agent agent = agentRepository.findById(agentId).orElseThrow(() -> new NotFoundException("Agent not found"));
		chatService.invalidateCacheByAgentId(agentId);
		List<String> tools = agent.getTools() == null ? new ArrayList<>() : new ArrayList<>(agent.getTools());
		tools.add(command.toolsId().toString());
		agent.setTools(tools);
		agentRepository.save(agent);
		mcpServerRepository.save(server);
	}

	McpServer findByAgentIdAndToolsId(UUID agentId, UUID toolsId) {
		return mcpServerRepository.findByAgentIdAndToolsId(agentId, toolsId).orElseThrow(() -> new NotFoundException("Mcp server not found"));
	}

	void updateEnv(UUID agentId, UUID toolsId, McpServerUpdate update) {
		McpServer server = findByAgentIdAndToolsId(agentId, toolsId);
		server.setUsedTools(update.usedTools());
		server.setEnv(update.env());
		chatService.invalidateCacheByAgentId(agentId);
		mcpServerRepository.save(server);
	}

	void deleteByAgentAndTool(UUID agentId, UUID toolsId) {
		McpServer server = findByAgentIdAndToolsId(agentId, toolsId);
		chatService.invalidateCacheByAgentId(agentId);
		mcpServerRepository.delete(server);
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new NotFoundException("Agent not found"));

		List<String> tools = agent.getTools();
		if (tools == null) {
			tools = new ArrayList<>();
		}

		tools.remove(toolsId.toString());
		agent.setTools(tools);
		agentRepository.save(agent);

	}
}
