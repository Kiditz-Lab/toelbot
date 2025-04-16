package com.toelbox.chatbot.facebook;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@Table("facebook_page")
class FacebookPage {
	@Id
	private UUID id;
	private UUID agentId;
	private String pageId;
	private String accessToken;
	private String name;
	private String category;
	private String imageUrl;
	private boolean active;

}
