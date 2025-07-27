package com.toelbox.chatbot.agent;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;

@AllArgsConstructor
class ChatAdvisor implements CallAdvisor, StreamAdvisor {
	private final ApplicationEventPublisher publisher;

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
		ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

		return chatClientResponse;
	}

	@Override
	public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}
}
