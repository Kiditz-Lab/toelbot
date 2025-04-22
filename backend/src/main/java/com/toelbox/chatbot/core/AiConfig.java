package com.toelbox.chatbot.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AiConfig {
	public static final String SYSTEM_PROMPT = """
			         You are a preprocessing assistant for LLM training from scraped webpages.
			         \s
			         Your tasks are:
			         1. Clean the markdown and retain only useful content (remove ads, navbars, etc.).
			         2. Preserve the **original information hierarchy** (headings, paragraphs, lists).
			         3. Extract all **important internal or external links** that are part of the content.
			         4. Return the output in this format:
			
			         ---
			         [Original Source](%s)
			
			         # Clean Content
			
			         (Cleaned markdown...)
			
			         # Extracted Links
			
			         - [Link Title](URL) - short description if available
			         - [Another Link](URL) - description...
			
			         **Important**:
			         - Only include links that are part of the meaningful content (not navbars, footers).
			         - Do NOT hallucinate links.
			         - Do not rephrase content heavily, just clean and clarify.
			""";

	@Bean(name = "grokOpenAIApi")
	OpenAiApi grokOpenAIApi(AIConfigProperties configProperties) {
		return OpenAiApi.builder()
				.apiKey(configProperties.getGrooqApiKey())
				.baseUrl(configProperties.getGrooqBaseUrl())
				.build();
	}

	@Bean(name = "openAIApi")
	OpenAiApi openAIApi(AIConfigProperties configProperties) {
		return OpenAiApi.builder()
				.apiKey(configProperties.getOpenAiApiKey())
				.build();
	}

	@Bean(name = "llama8bChatClient")
	ChatClient llama8bChatClient(@Qualifier("grokOpenAIApi") OpenAiApi openAiApi) {
		var chatModel = OpenAiChatModel
				.builder().defaultOptions(OpenAiChatOptions.builder()
						.model("llama3-8b-8192")
						.build())
				.openAiApi(openAiApi)
				.build();
		return ChatClient.builder(chatModel)
				.defaultSystem(SYSTEM_PROMPT)
				.build();
	}


	@Bean(name = "llama70bChatClient")
	ChatClient llama70bChatClient(@Qualifier("grokOpenAIApi") OpenAiApi openAiApi) {
		var chatModel = OpenAiChatModel
				.builder().defaultOptions(OpenAiChatOptions.builder()
						.model("deepseek-r1-distill-qwen-32b")
						.build())
				.openAiApi(openAiApi)
				.build();

		return ChatClient.builder(chatModel)
				.defaultSystem(SYSTEM_PROMPT)
				.build();
	}

	@Bean(name = "gpt4ChatClient")
	ChatClient gpt4ChatClient(@Qualifier("openAIApi") OpenAiApi openAiApi) {
		var chatModel = OpenAiChatModel
				.builder().defaultOptions(OpenAiChatOptions.builder()
						.model("gpt-3.5-turbo")
						.build())
				.openAiApi(openAiApi)
				.build();

		return ChatClient.builder(chatModel)
				.defaultSystem(SYSTEM_PROMPT)
				.build();
	}


	@Bean
	InMemoryChatMemory chatMemory(){
		return new InMemoryChatMemory();
	}
}
