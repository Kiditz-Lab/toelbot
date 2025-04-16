package com.toelbox.chatbot.instagram;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@Table("instagram_account")
class InstagramAccount {
	@Id
	private UUID id;
	private UUID agentId;
	private String userId;
	private String profilePictureUrl;
	private String name;
	private String token;
	private String username;
}