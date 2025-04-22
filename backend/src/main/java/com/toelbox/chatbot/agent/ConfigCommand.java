package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.ModelVendor;

record ConfigCommand(
		String aiModel,
		ModelVendor vendor,
		String prompt,
		double temperature
) {

}
