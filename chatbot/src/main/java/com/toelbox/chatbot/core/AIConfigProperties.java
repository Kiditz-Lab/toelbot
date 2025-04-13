package com.toelbox.chatbot.core;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bot")
@Data
@ToString
public class AIConfigProperties {
	private String grooqBaseUrl;
	private String grooqApiKey;
	private String openAiApiKey;
}
