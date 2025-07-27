package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.Country;
import com.toelbox.chatbot.facebook.FacebookIncomingMessageEvent;
import com.toelbox.chatbot.facebook.FacebookReplyMessageEvent;
import com.toelbox.chatbot.instagram.InstagramIncomingMessageEvent;
import com.toelbox.chatbot.instagram.InstagramReplyMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class IntegrationChatService {
	private final ConversationChatService chatService;
	private final AgentQueryService queryService;
	private final ApplicationEventPublisher publisher;

	@Async
	@EventListener
	void facebookChat(FacebookIncomingMessageEvent event) {
		var agent = queryService.findById(event.agentId());
		var agentChat = new AgentChat(event.text(), event.senderId());
		String response = chatService.syncChat(agent, agentChat, new Country());
		publisher.publishEvent(new FacebookReplyMessageEvent(event.pageId(), event.senderId(), response));
	}

	@Async
	@EventListener
	void instagramChat(InstagramIncomingMessageEvent event) {
		var agent = queryService.findById(event.agentId());
		var agentChat = new AgentChat(event.text(), event.senderId());
		String response = chatService.syncChat(agent, agentChat, new Country());
		publisher.publishEvent(new InstagramReplyMessageEvent(event.recipientId(), event.senderId(), response));
	}
}
