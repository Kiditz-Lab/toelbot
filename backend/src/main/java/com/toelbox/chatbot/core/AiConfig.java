package com.toelbox.chatbot.core;

import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AiConfig {

	@Bean
	InMemoryChatMemory chatMemory(){
		return new InMemoryChatMemory();
	}
}
