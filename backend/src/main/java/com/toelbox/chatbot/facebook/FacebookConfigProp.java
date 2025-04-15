package com.toelbox.chatbot.facebook;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("facebook")
@Data
@ToString
class FacebookConfigProp {
	private String verifyToken;
	private String appId;
	private String appSecret;
	private String redirectUri;
}
