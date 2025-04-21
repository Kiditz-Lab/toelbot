package com.toelbox.chatbot.agent;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("mcp_servers")
class McpServer {
	@Id
	private UUID id;
	private UUID agentId;
	@Column("tool_id")
	private UUID toolsId;
	private String serverName;
	private String command;
	private List<String> args;
	private Map<String, String> env;
	@MappedCollection(idColumn = "server_id")
	private Set<UsedTool> usedTools;

	@Transient
	McpSyncClient toSyncClient() {
		ServerParameters params = ServerParameters.builder(command)
				.args(args)
				.env(env)
				.build();
		var transport = new StdioClientTransport(params);
		var client = McpClient.sync(transport)
				.requestTimeout(Duration.ofSeconds(60))
				.build();
		client.initialize();
		return client;
	}

	@Transient
	McpAsyncClient toASyncClient() {
		ServerParameters params = ServerParameters.builder(command)
				.args(args)
				.env(env)
				.build();
		var transport = new StdioClientTransport(params);
		var client = McpClient.async(transport)
				.requestTimeout(Duration.ofSeconds(20))
				.build();
		client.initialize();
		return client;
	}
}

