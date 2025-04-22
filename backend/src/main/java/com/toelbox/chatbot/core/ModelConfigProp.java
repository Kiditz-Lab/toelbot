package com.toelbox.chatbot.core;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "model")
@Data
@ToString
public class ModelConfigProp {
	private String grooqBaseUrl;
	private String deepseekApiKey;
	private String deepseekBaseUrl;
	private String grooqApiKey;
	private String openAiApiKey;
}
