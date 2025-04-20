package com.toelbox.chatbot.tools;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Table("tools")
public class Tools {

	@Id
	private UUID id;
	private String name;
	private String command;
	private String imageUrl;
	private String description;
	private String shortDescription;
	private List<String> args;
	private Map<String, String> env;

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
}
