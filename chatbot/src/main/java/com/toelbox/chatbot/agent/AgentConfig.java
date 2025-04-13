package com.toelbox.chatbot.agent;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AgentConfig {

	private AIModel aiModel;
	private String prompt;
	private double temperature;
}
