package com.toelbox.chatbot.agent;

import org.springframework.data.relational.core.mapping.Table;

@Table
public record UsedTool(String name, String description) {
	@Override
	public String toString() {
		return "Tool{name='" + name + "', description='" + description + "'}";
	}
}
