package com.toelbox.chatbot.history;

import com.toelbox.chatbot.core.Country;

import java.util.UUID;

public record ChatHistoryEvent(
		String userMessage,
		String botMessage,
		Country country,
		String model,
		UUID agentId,
		String chatId
) {
}
