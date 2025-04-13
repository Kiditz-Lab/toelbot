package com.toelbox.chatbot.tools;

import com.toelbox.chatbot.core.NotFoundException;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
class ToolsCommandService {
	private final ToolsRepository repository;

	Tools createTool(ToolsCommand command) {
		Tools tool = Tools.builder()
				.name(command.name())
				.shortDescription(command.shortDescription())
				.description(command.description())
				.imageUrl(command.imageUrl())
				.args(command.args())
				.command(command.command())
				.env(command.env())
				.build();
		return repository.save(tool);
	}

	void updateTool(UUID id, ToolsCommand command) {
		repository.findById(id).map(tool -> {
			tool.setName(command.name());
			tool.setDescription(command.description());
			tool.setImageUrl(command.imageUrl());
			tool.setArgs(command.args());
			tool.setCommand(command.command());
			tool.setEnv(command.env());
			return repository.save(tool);
		}).orElseThrow(() -> new NotFoundException("Tools not found by id %s".formatted(id)));
	}

	McpSchema.CallToolResult testTool(TestToolCommand command) {
		McpSyncClient client = command.toSyncClient();
		client.initialize();
		var res = client.callTool(new McpSchema.CallToolRequest(command.name(), command.params()));
		log.info("Result >>> {}", res);
		client.closeGracefully();
		return res;
	}

	void deleteTool(UUID id) {
		repository.deleteById(id);
	}
}
