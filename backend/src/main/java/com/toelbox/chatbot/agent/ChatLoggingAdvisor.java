package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
class ChatLoggingAdvisor implements StreamAroundAdvisor, CallAroundAdvisor {
	private final AdvisorInfo info;
	private final ApplicationEventPublisher publisher;

	@Override
	public @NonNull Flux<AdvisedResponse> aroundStream(@NonNull AdvisedRequest advisedRequest, @NonNull StreamAroundAdvisorChain chain) {
		String userMessage = advisedRequest.userText();
		log.info("\nRequest: {}", userMessage);
		Flux<AdvisedResponse> responses = chain.nextAroundStream(advisedRequest);
		return new MessageAggregator().aggregateAdvisedResponse(responses, response -> {
			assert response.response() != null;
			String botMessage = response.response().getResult().getOutput().getText();
			publisher.publishEvent(new ChatHistoryEvent(userMessage, botMessage, info.country, info.model, info.agentId, info.chatId));
			log.info("\nResponse: {}", response.response().getResult().getOutput().getText());
		});
	}


	@Override
	public @NonNull String getName() {
		return "StreamAdvisor";
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public @NonNull AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
		String userMessage = advisedRequest.userText();
		AdvisedResponse resp = chain.nextAroundCall(advisedRequest);
		String botMessage = Objects.requireNonNull(resp.response()).getResult().getOutput().getText();
		publisher.publishEvent(new ChatHistoryEvent(userMessage, botMessage, info.country, info.model, info.agentId, info.chatId));
		return resp;
	}

	record AdvisorInfo(Country country, String model, String chatId, UUID agentId) {

	}
}
