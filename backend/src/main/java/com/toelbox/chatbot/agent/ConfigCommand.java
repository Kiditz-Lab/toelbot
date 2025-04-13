package com.toelbox.chatbot.agent;

record ConfigCommand(
		AIModel aiModel,
		String prompt,
		double temperature
) {

}
