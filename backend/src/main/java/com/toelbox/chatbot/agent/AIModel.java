package com.toelbox.chatbot.agent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum AIModel {
	LLAMA_3_70B_8192("Meta LLAMA 3 70 B 8192", "llama3-70b-8192"),
	GPT_4O("GPT-4o", "gpt-4o-2024-08-06"),
	GPT_4_5_PREVIEW("GPT-4.5", "gpt-4.5-preview-2025-02-27"),
	GPT_4O_MINI("GPT-4o mini", "gpt-4o-mini"),
	GPT_3_5_TURBO("GPT-3.5 Turbo", "gpt-3.5-turbo");
	private final String name;
	private final String version;
}
