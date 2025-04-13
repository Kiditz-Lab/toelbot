package com.toelbox.chatbot.tools;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Transient;

import java.time.Duration;
import java.util.List;
import java.util.Map;


record TestToolCommand(
		@NotBlank
		String command,
		@NotEmpty
		List<String> args,
		Map<String, String> env,
		String name,
		Map<String, Object> params
) {
	McpSyncClient toSyncClient() {
		ServerParameters params = ServerParameters.builder(command)
				.args(args)
				.env(env)
				.build();
		var transport = new StdioClientTransport(params);
		var client = McpClient.sync(transport)
				.requestTimeout(Duration.ofSeconds(20))
				.build();
		client.initialize();
		return client;
	}
}
