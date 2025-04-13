package com.toelbox.chatbot.agent;

import com.github.benmanes.caffeine.cache.Cache;
import com.toelbox.chatbot.core.AIConfigProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@Slf4j
@RequiredArgsConstructor
class AgentChatService {
	private final AgentQueryService service;
	private final VectorStore vectorStore;
	private final AIConfigProperties configProperties;
	private final ApplicationEventPublisher publisher;
	private final McpQueryService mcpService;
	private final InMemoryChatMemory chatMemory;
	private final Cache<String, ChatClient> chatClientCache;
	private final ConcurrentHashMap<UUID, Set<String>> agentIdToChatIdsMap = new ConcurrentHashMap<>();

	@Transactional
	Flux<String> chat(UUID agentId, AgentChat chat, HttpServletRequest request, Principal principal) throws Exception {
		log.info("Agent Id: {}", agentId);
		log.info("Chat ID: {}", chat.chatId());
		Agent agent = service.findById(agentId);
		if (principal == null && !agent.isPublic()) {
			throw new AccessDeniedException("Your agent is not public");
		}
//		String ipAddress = IpAddress.getClientIp(request);
		agentIdToChatIdsMap.compute(agentId, (key, existingSet) -> {
			Set<String> newSet = existingSet == null ? ConcurrentHashMap.newKeySet() : existingSet;
			newSet.add(chat.chatId());
			return newSet;
		});

		ChatClient client = chatClientCache.get(chat.chatId(), id -> buildChatClient(agent));
		return client.prompt()
				.user(chat.chat())
				.advisors(a -> a
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chat.chatId())
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 500))
				.stream().content();
	}
	List<Map<String, String>> aiModels() {
		return Arrays.stream(AIModel.values()).map(aiModel -> Map.of("name", aiModel.getName(), "version", aiModel.getVersion(), "value", aiModel.name())).collect(Collectors.toList());
	}

	ChatClient buildChatClient(Agent agent) {
		var config = agent.getConfig();
		var model = config.getAiModel();
		var api = openAiApi(isGrooq(model));
		var clients = mcpService.findAllByAgentClientSync(agent.getId());
		var tools = SyncMcpToolCallbackProvider.syncToolCallbacks(  clients);
		log.info("Clients: {}", clients.size());
		var options = OpenAiChatOptions.builder()
				.model(model.getVersion())
				.temperature(config.getTemperature())
				.build();
		var chatModel = OpenAiChatModel
				.builder()
				.openAiApi(api)
				.defaultOptions(options)
				.build();
		var builder = ChatClient.builder(chatModel)
				.defaultTools(tools)
				.defaultAdvisors(
						new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder()
								.filterExpression("agentId == '%s'".formatted(agent.getId().toString()))
								.build()
						),
						new MessageChatMemoryAdvisor(chatMemory)
				);
		var prompt = config.getPrompt();
		if (StringUtils.isNoneBlank(prompt)) {
			builder = builder.defaultSystem(prompt);
		}
		return builder.build();
	}

	void invalidateCacheByAgentId(UUID agentId) {
		Set<String> chatIds = agentIdToChatIdsMap.get(agentId);

		if (chatIds != null) {
			Set<String> chatIdsCopy = new HashSet<>(chatIds);
			for (String chatId : chatIdsCopy) {
				chatClientCache.invalidate(chatId);
			}
			agentIdToChatIdsMap.remove(agentId, chatIds);
			log.info("Invalidated {} cache entries for agentId: {}", chatIdsCopy.size(), agentId);
		}
	}

	private String getPrompt(AgentConfig config) {
		String prompt = config.getPrompt();
		prompt += """
				---
				Additional Instructions:
				- If the user asks about listings (e.g., properties, products, services), return a structured Markdown response.
				- Do NOT fabricate or infer external URLs.
				- Do NOT show the `url` field if no real URL is available.
				- Do NOT hallucinate for url.
				- For non-listing queries, respond in friendly markdown or plain text.
				""";

		log.debug("Prompt : {}", prompt);
		return prompt;
	}

//	@Bean(destroyMethod = "close")
//	public McpSyncClient mcpClient() {
//		var stdioParams = ServerParameters.builder("npx")
//				.args("-y", "@modelcontextprotocol/server-filesystem")
//				.env(Map.of("ODOO_URL", "abc"))
//				.build();
//		var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
//				.requestTimeout(Duration.ofSeconds(10)).build();
//
//		var init = mcpClient.initialize();
//
//		System.out.println("MCP Initialized: " + init);
//
//		return mcpClient;
//
//	}

	boolean isGrooq(AIModel model) {
		return switch (model) {
			case GEMMA2_9B_IT, LLAMA_3_8B_8192, LLAMA_3_70B_8192 -> true;
			case GPT_4O, GPT_4O_MINI, GPT_3_5_TURBO, GPT_4_5_PREVIEW -> false;
		};
	}

	OpenAiApi openAiApi(boolean isGrooq) {
		if (isGrooq) {
			return OpenAiApi.builder()
					.apiKey(configProperties.getGrooqApiKey())
					.baseUrl(configProperties.getGrooqBaseUrl())
					.build();
		}
		return OpenAiApi.builder()
				.apiKey(configProperties.getOpenAiApiKey())
				.build();
	}
}
