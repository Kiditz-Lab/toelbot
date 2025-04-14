package com.toelbox.chatbot.tools;

import com.toelbox.chatbot.core.NotFoundException;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ToolsQueryService {
	private final ToolsRepository repository;
	@Cacheable(value = "toolsCache", key = "#id")
	ToolsResponse findById(UUID id) {
		Tools tool = repository.findById(id).orElseThrow(() -> new NotFoundException("Tool not found"));
		var client = tool.toSyncClient();
		McpSchema.ListToolsResult mcpTools = client.listTools();
		client.closeGracefully();
		return new ToolsResponse(tool.getId(), tool.getName(), tool.getImageUrl(), tool.getDescription(), tool.getShortDescription(), mcpTools.tools(), tool.getCommand(), tool.getArgs(), tool.getEnv());
	}

	List<ToolListResponse> filterTools(String name, Sort sort) {
		return repository.findAllByNameContainingIgnoreCase(name, sort);
	}
}
