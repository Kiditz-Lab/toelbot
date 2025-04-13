package com.toelbox.chatbot.agent;

import java.util.UUID;

public record ChatHistoryEvent(
		String userMessage,
		String botMessage,
		String ipAddress,
		String model,
		UUID agentId,
		String chatId
) {
}
