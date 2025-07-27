package com.toelbox.chatbot.agent;

import com.github.benmanes.caffeine.cache.Cache;
import com.toelbox.chatbot.core.Country;
import com.toelbox.chatbot.core.ModelConfigProp;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
class ConversationChatService {
	public static final String CHAT_MEMORY_CONVERSATION_ID_KEY = "CHAT_MEMORY_CONVERSATION_ID_KEY";
	public static final String CHAT_MEMORY_RETRIEVE_SIZE_KEY = "CHAT_MEMORY_RETRIEVE_SIZE_KEY";
	private final VectorStore vectorStore;
	private final ModelConfigProp configProperties;
	private final McpQueryService mcpService;
	private final ChatMemory chatMemory;
	private final ChatModelService modelService;
	private final Cache<String, ChatClient> chatClientCache;
	private final ConcurrentHashMap<UUID, Set<String>> agentIdToChatIdsMap = new ConcurrentHashMap<>();


	Flux<String> asyncChat(Agent agent, Conversation chat, Country country) {
		String chatId = chat.chatId();
		agentIdToChatIdsMap.compute(agent.getId(), (key, existingSet) -> {
			Set<String> newSet = existingSet == null ? ConcurrentHashMap.newKeySet() : existingSet;
			newSet.add(chat.chatId());
			return newSet;
		});

		ChatClient client = chatClientCache.get(chat.chatId(), id -> buildChatClient(agent, chatId, country));
		return client.prompt()
				.system(s ->
						s.param("current_date", LocalDateTime.now().toString())
								.param("agent_id", agent.getId().toString())
								.param("chat_id", chatId)
				)
				.user(chat.chat())
				.advisors(a -> a
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chat.chatId())
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 500))
				.stream().content();
	}

	String syncChat(Agent agent, Conversation chat, Country country) {
		String chatId = chat.chatId();
		agentIdToChatIdsMap.compute(agent.getId(), (key, existingSet) -> {
			Set<String> newSet = existingSet == null ? ConcurrentHashMap.newKeySet() : existingSet;
			newSet.add(chat.chatId());
			return newSet;
		});

		ChatClient client = chatClientCache.get(chat.chatId(), id -> buildChatClient(agent, chatId, country));
		return client.prompt()
				.system(s ->
						s.param("current_date", LocalDateTime.now().toString())
								.param("agent_id", agent.getId().toString())
								.param("chat_id", chatId)
				)
				.user(chat.chat())
				.advisors(a -> a
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chat.chatId())
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 500))
				.call().content();
	}

	List<Map<String, String>> aiModels() {
		return Arrays.stream(AIModel.values()).map(aiModel -> Map.of("name", aiModel.getName(), "version", aiModel.getVersion(), "value", aiModel.name())).collect(Collectors.toList());
	}

	ChatClient buildChatClient(Agent agent, String chatId, Country country) {
		log.info("DATA : {}, {}", chatId, country);
		var config = agent.getConfig();
		var servers = mcpService.findByAgentId(agent.getId());
		List<McpSyncClient> clients = servers.stream().map(McpServer::toSyncClient).toList();
		var usedTools = servers.stream()
				.flatMap(server -> server.getUsedTools().stream())
				.collect(Collectors.toSet());

		var tools = new CustomSyncMcpToolCallbackProvider(clients, usedTools);
		List<Advisor> advisors = new ArrayList<>();

		advisors.add(
				QuestionAnswerAdvisor.builder(vectorStore)
						.searchRequest(SearchRequest.builder()
								.filterExpression("agentId == '%s'".formatted(agent.getId().toString()))
								.build())
						.build()
		);

		advisors.add(MessageChatMemoryAdvisor.builder(chatMemory).build());
		advisors.add(new SimpleLoggerAdvisor());

		var builder = ChatClient.builder(modelService.getChatModel(agent.getConfig().getVendor(), agent.getConfig().getAiModel(), agent.getConfig().getTemperature()))
				.defaultAdvisors(advisors);
		var prompt = config.getPrompt() + "\n Today is {current_date}.\n Agent Id is {agent_id}.\n chat id is {chat_id}";
		if (StringUtils.isNoneBlank(prompt)) {
			builder = builder.defaultSystem(prompt);
		}
		if(tools.getToolCallbacks().length != 0){
			builder = builder.defaultTools(tools);
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


	boolean isGrooq(AIModel model) {
		return switch (model) {
			case LLAMA_3_70B_8192 -> true;
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
