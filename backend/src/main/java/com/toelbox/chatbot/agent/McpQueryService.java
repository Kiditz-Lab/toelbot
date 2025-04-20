package com.toelbox.chatbot.agent;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class McpQueryService {
	private final McpServerRepository repository;

	List<McpServerResponse> findAllByAgent(UUID agentId) {
		var mcpServers = repository.findByAgentId(agentId);
		return mcpServers.stream().map(saved -> new McpServerResponse(
				saved.getId(),
				saved.getServerName(),
				saved.getCommand(),
				saved.getArgs(),
				saved.getEnv()
		)).collect(Collectors.toList());
	}

	List<McpServer> findByAgentId(UUID agentId) {
		return repository.findByAgentId(agentId);
	}

	List<McpSyncClient> findAllByAgentClientSync(UUID agentId) {
		var mcpServers = repository.findByAgentId(agentId);
		return mcpServers.stream().map(McpServer::toSyncClient).collect(Collectors.toList());
	}
	List<McpAsyncClient> findAllByAgentClientASync(UUID agentId) {
		var mcpServers = repository.findByAgentId(agentId);
		return mcpServers.stream().map(McpServer::toASyncClient).collect(Collectors.toList());
	}


}
