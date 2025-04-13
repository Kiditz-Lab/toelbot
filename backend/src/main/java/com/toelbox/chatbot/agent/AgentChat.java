package com.toelbox.chatbot.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

record AgentChat(
		@NotBlank
		String chat,
		@NotEmpty
		String chatId
) {
}
