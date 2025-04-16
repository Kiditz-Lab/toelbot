package com.toelbox.chatbot.instagram;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("instagram")
@Data
@ToString
class InstagramConfigProp {
	private String verifyToken;
	private String appId;
	private String appSecret;
	private String redirectUri;
	private String targetOrigin;
}

