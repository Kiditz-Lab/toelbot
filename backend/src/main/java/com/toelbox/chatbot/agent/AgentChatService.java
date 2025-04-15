package com.toelbox.chatbot.agent;

import com.github.benmanes.caffeine.cache.Cache;
import com.toelbox.chatbot.core.AIConfigProperties;
import com.toelbox.chatbot.core.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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


	Flux<String> asyncChat(Agent agent, AgentChat chat, Country country) {
		String chatId = chat.chatId();
		agentIdToChatIdsMap.compute(agent.getId(), (key, existingSet) -> {
			Set<String> newSet = existingSet == null ? ConcurrentHashMap.newKeySet() : existingSet;
			newSet.add(chat.chatId());
			return newSet;
		});

		ChatClient client = chatClientCache.get(chat.chatId(), id -> buildChatClient(agent, chatId, country));
		return client.prompt()
				.user(chat.chat())
				.advisors(a -> a
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chat.chatId())
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 500))
				.stream().content();
	}

	String syncChat(Agent agent, AgentChat chat, Country country) {
		String chatId = chat.chatId();
		log.info("CHATID >>>> {}", chatId);
		agentIdToChatIdsMap.compute(agent.getId(), (key, existingSet) -> {
			Set<String> newSet = existingSet == null ? ConcurrentHashMap.newKeySet() : existingSet;
			newSet.add(chat.chatId());
			return newSet;
		});

		ChatClient client = chatClientCache.get(chat.chatId(), id -> buildChatClient(agent, chatId, country));
		return client.prompt()
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
		log.info("DATA : {}", chatId, country);
		var config = agent.getConfig();
		var model = config.getAiModel();
		var api = openAiApi(isGrooq(model));

		var clients = mcpService.findAllByAgentClientSync(agent.getId());
		var tools = SyncMcpToolCallbackProvider.syncToolCallbacks(clients);
		var options = OpenAiChatOptions.builder()
				.model(model.getVersion())
				.temperature(config.getTemperature())
				.build();
		var chatModel = OpenAiChatModel
				.builder()
				.openAiApi(api)
				.defaultOptions(options)
				.build();
		List<Advisor> advisors = new ArrayList<>();
		advisors.add(
				new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder()
						.filterExpression("agentId == '%s'".formatted(agent.getId().toString()))
						.build()
				)
		);
		advisors.add(new MessageChatMemoryAdvisor(chatMemory));
//		var info = new ChatLoggingAdvisor.AdvisorInfo(country, agent.getConfig().getAiModel().getVersion(), chatId, agent.getId());
//		advisors.add(new ChatLoggingAdvisor(info, publisher));

		var builder = ChatClient.builder(chatModel)
				.defaultTools(tools)
				.defaultAdvisors(advisors);
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
