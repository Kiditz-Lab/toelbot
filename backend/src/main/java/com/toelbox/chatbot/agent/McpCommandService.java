package com.toelbox.chatbot.agent;

import groovy.util.logging.Slf4j;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class McpCommandService {
	private final McpServerRepository repository;

	McpServerResponse create(UUID agentId, McpServerCommand request) {
		var server = McpServer.builder()
				.serverName(request.serverName())
				.command(request.command())
				.args(request.args())
				.env(request.env())
				.agentId(agentId)
				.build();

		var saved = repository.save(server);

		return new McpServerResponse(
				saved.getId(),
				saved.getServerName(),
				saved.getCommand(),
				saved.getArgs(),
				saved.getEnv()
		);
	}

	McpSchema.ListToolsResult listTools(McpServerCommand request) {
		var server = McpServer.builder()
				.serverName(request.serverName())
				.command(request.command())
				.args(request.args())
				.env(request.env())
				.build();
		var client = server.toSyncClient();
		client.initialize();
		McpSchema.ListToolsResult result = client.listTools();
		client.closeGracefully();
		return result;
	}

}
