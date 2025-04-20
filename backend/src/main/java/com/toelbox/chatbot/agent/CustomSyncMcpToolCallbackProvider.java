package com.toelbox.chatbot.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.modelcontextprotocol.client.McpSyncClient;

import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.util.ToolUtils;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of {@link ToolCallbackProvider} that discovers and provides MCP tools
 * from one or more MCP servers.
 */
public class CustomSyncMcpToolCallbackProvider implements ToolCallbackProvider {

	private final List<McpSyncClient> mcpClients;

	private final Set<UsedTool> usedTools;

	public CustomSyncMcpToolCallbackProvider(List<McpSyncClient> mcpClients, Set<UsedTool> usedTools) {
		this.mcpClients = mcpClients;
		this.usedTools = usedTools;
	}

	public CustomSyncMcpToolCallbackProvider(McpSyncClient... mcpClients) {
		this.mcpClients = List.of(mcpClients);
		this.usedTools = Set.of();
	}

	@Override
	public ToolCallback[] getToolCallbacks() {
		var toolCallbacks = new ArrayList<ToolCallback>();

		mcpClients.forEach(mcpClient -> {
			toolCallbacks.addAll(mcpClient.listTools()
					.tools()
					.stream()
					.filter(tool -> filterTool(tool.name()))
					.map(tool -> {
						String toolDescription = findCustomDescription(tool.name());
						return new SyncMcpToolCallback(mcpClient, new McpSchema.Tool(tool.name(), toolDescription, tool.inputSchema()));
					})
					.toList());
		});

		var array = toolCallbacks.toArray(new ToolCallback[0]);
		validateToolCallbacks(array);
		return array;
	}

	private String findCustomDescription(String toolName) {
		return usedTools.stream()
				.filter(customTool -> customTool.name().equals(toolName))
				.map(UsedTool::description)
				.findFirst()
				.orElse(null);
	}

	private boolean filterTool(String toolName) {
		if (usedTools.isEmpty()) {
			return true; // if no customTools configured, allow all
		}
		return usedTools.stream().anyMatch(customTool -> customTool.name().equals(toolName));
	}


	/**
	 * Validates that there are no duplicate tool names in the provided callbacks.
	 *
	 * @param toolCallbacks the tool callbacks to validate
	 * @throws IllegalStateException if duplicate tool names are found
	 */
	private void validateToolCallbacks(ToolCallback[] toolCallbacks) {
		List<String> duplicateToolNames = ToolUtils.getDuplicateToolNames(toolCallbacks);
		if (!duplicateToolNames.isEmpty()) {
			throw new IllegalStateException(
					"Multiple tools with the same name (%s)".formatted(String.join(", ", duplicateToolNames)));
		}
	}

	/**
	 * Creates a consolidated list of tool callbacks from multiple MCP clients.
	 *
	 * @param mcpClients       the list of MCP clients to create callbacks from
	 * @param usedTools a map of tool names and descriptions
	 * @return a list of tool callbacks from all provided clients
	 */
	public static List<ToolCallback> syncToolCallbacks(List<McpSyncClient> mcpClients, Set<UsedTool> usedTools) {
		if (CollectionUtils.isEmpty(mcpClients)) {
			return List.of();
		}
		return List.of(new CustomSyncMcpToolCallbackProvider(mcpClients, usedTools).getToolCallbacks());
	}


}
