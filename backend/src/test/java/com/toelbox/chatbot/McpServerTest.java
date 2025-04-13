package com.toelbox.chatbot;


import groovy.util.logging.Slf4j;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

@Slf4j
class McpServerTest {

	public static void main(String[] args) {
		ServerParameters params = ServerParameters.builder("uv")
				.args("run",
						"--with",
						"mcp[cli]",
						"mcp",
						"run",
						"/Users/rifkyaditya/Playground/mcp/odoo-mcp/server.py")
				.env(Map.of(
						"ODOO_URL", "http://localhost:8069",
						"ODOO_DB", "store",
						"ODOO_EMAIL", "kiditzbastara@gmail.com",
						"ODOO_API_KEY", "897b0d3bad1a2e0e3e5ca8ed62e30b3b8c896f43"
				))

				.build();
		var transport = new StdioClientTransport(params);
		McpSchema.Implementation clientInfo = new McpSchema.Implementation("MCP Schema", "1.0.0");

		McpAsyncClient client = McpClient.async(transport)
				.clientInfo(clientInfo)
				.capabilities(McpSchema.ClientCapabilities.builder()
						.roots(true)
						.build())
				.build();
		McpSchema.InitializeResult result = client.initialize().block();
//		System.out.println(result.protocolVersion());
//		System.out.println(client.getClientCapabilities());
		client.listTools().doOnNext(System.out::println).subscribe();
//		McpSchema.CallToolRequest callToolRequest = new McpSchema.CallToolRequest("get_products", Map.of("name", "ibanez"));
//		client.callTool(callToolRequest).doOnNext(System.out::println).subscribe();
//		client.closeGracefully();
//		transport.closeGracefully();

	}


}
