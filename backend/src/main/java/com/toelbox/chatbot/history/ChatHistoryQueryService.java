package com.toelbox.chatbot.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class ChatHistoryQueryService {
	private final ChatHistoryRepository repository;

	public Page<ChatHistory> findByFilters(UUID agentId, ChatHistoryFilter filter, Pageable pageable) {
		log.info("Filter: {}", filter);
		log.info("Pageable: {}", pageable);

		return repository.findByFilters(agentId, filter, pageable);

	}


}
