package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.Country;
import com.toelbox.chatbot.history.ChatHistory;
import com.toelbox.chatbot.history.ChatHistoryEvent;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

class ChatAdvisor implements BaseAdvisor {
	private ApplicationEventPublisher publisher;
	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
		var history = new ChatHistory();
		history.setId(UUID.randomUUID());
		history.setBotMessage(chatClientRequest.prompt().getContents());
		return null;
	}

	@Override
	public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
		return null;
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
