package com.toelbox.chatbot.history;

import com.toelbox.chatbot.core.IpApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
class ChatHistoryCommandService {
	private final ChatHistoryRepository repository;
	private final IpApiService service;

	@Async
	@EventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	void history(ChatHistoryEvent event) {
//		IpApiResponse address = service.getIpInfo(event.ipAddress());
		ChatHistory history = ChatHistory.builder()
				.agentId(event.agentId())
				.chatId(event.chatId())
				.model(event.model())
				.botMessage(event.botMessage())
				.userMessage(event.userMessage())
//				.country(address.getCountry())
				.countryCode(event.country().getCode())
//				.regionCode(address.getRegion())
//				.regionName(address.getRegionName())
//				.latitude(address.getLat())
//				.longitude(address.getLon())
//				.ipAddress(event.ipAddress())
				.createdAt(LocalDateTime.now())
				.build();
		repository.save(history);
	}
}
