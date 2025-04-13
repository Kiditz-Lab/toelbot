package com.toelbox.chatbot.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
class HtmlService {
	private final ChatClient chatClient;

	HtmlService(@Qualifier("llama70bChatClient") ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	String convertToMarkdown(String markdown) {
		return chatClient.prompt().user(markdown).call().content();
	}


}
