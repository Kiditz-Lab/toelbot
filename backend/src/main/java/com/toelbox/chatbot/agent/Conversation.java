package com.toelbox.chatbot.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

record Conversation(
		@NotBlank
		String chat,
		@NotEmpty
		String chatId
) {
}
