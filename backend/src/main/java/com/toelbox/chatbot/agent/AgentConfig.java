package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.ModelVendor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AgentConfig {
	private String aiModel;
	private ModelVendor vendor;
	private String prompt;
	private double temperature;
}
