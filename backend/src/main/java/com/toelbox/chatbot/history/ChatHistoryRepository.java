package com.toelbox.chatbot.history;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ChatHistoryRepository extends CrudRepository<ChatHistory, UUID>, ChatHistoryRepositoryCustom {
}