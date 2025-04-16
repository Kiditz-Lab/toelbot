package com.toelbox.chatbot.instagram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class InstagramProfile {
	@JsonProperty("account_type")
	private String accountType;
	@JsonProperty("profile_picture_url")
	private String profilePictureUrl;
	@JsonProperty("name")
	private String name;
	@JsonProperty("id")
	private String id;


}