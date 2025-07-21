package com.toelbox.chatbot.agent;

import com.google.cloud.vertexai.VertexAI;
import com.toelbox.chatbot.core.ModelVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChatModelService {

	@Value("${spring.ai.vertex.ai.gemini.projectId}")
	private String projectId;

	@Value("${spring.ai.openai.api-key}")
	private String openAiApiKey;

	@Value("${spring.ai.openai.deepseek-api-key}")
	private String deepseekApiKey;

	@Value("${spring.ai.openai.deepseek-base-url}")
	private String deepseekBaseUrl;

	@Value("${spring.ai.anthropic.api-key}")
	private String anthropicApiKey;

	@Value("${spring.ai.vertex.ai.gemini.location}")
	private String location;

	ChatModel getChatModel(ModelVendor vendor, String name, double temperature) {
		return switch (vendor) {
			case GOOGLE -> getVertexChatModel(name, temperature);
			case OPEN_AI -> getOpenAiChatModel(vendor, name, temperature);
			case ANTHROPIC -> getAnthropicChatModel(name, temperature);
			default -> null;
		};
	}

	private ChatModel getAnthropicChatModel(String name, double temperature) {
		AnthropicApi anthropicApi = AnthropicApi.builder().apiKey(anthropicApiKey).build();
		return AnthropicChatModel.builder()
				.anthropicApi(anthropicApi)
				.defaultOptions(AnthropicChatOptions.builder()
						.model(name)
						.temperature(temperature)
						.build())
				.build();
	}

	private ChatModel getVertexChatModel(String name, double temperature) {
		VertexAI vertexApi = new VertexAI(projectId, location);
		return VertexAiGeminiChatModel.builder()
				.vertexAI(vertexApi)
				.defaultOptions(VertexAiGeminiChatOptions.builder()
						.model(name)
						.temperature(temperature)
						.build())
				.build();
	}

	private ChatModel getOpenAiChatModel(ModelVendor vendor, String name, double temperature) {
		OpenAiApi api = switch (vendor) {
			case OPEN_AI -> OpenAiApi.builder()
					.apiKey(openAiApiKey)
					.build();
			case DEEPSEEK -> OpenAiApi.builder()
					.apiKey(deepseekApiKey)
					.baseUrl(deepseekBaseUrl)
					.build();
			default -> null;
		};
		return OpenAiChatModel.builder()
				.openAiApi(api)
				.defaultOptions(OpenAiChatOptions.builder()
						.model(name)
						.temperature(temperature)
						.build())
				.build();
	}

}
