package com.toelbox.chatbot.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record AgentCommand(
		@NotBlank
		@Size(max = 20)
		String name,
		boolean isPublic
) {

}
