package com.toelbox.chatbot.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

interface ChatHistoryRepositoryCustom {
	Page<ChatHistory> findByFilters(UUID agentId, ChatHistoryFilter filter, Pageable pageable);
}