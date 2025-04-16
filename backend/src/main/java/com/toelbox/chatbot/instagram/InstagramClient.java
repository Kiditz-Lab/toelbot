package com.toelbox.chatbot.instagram;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "instagramClient", url = "https://api.instagram.com", configuration = FeignConfig.class)
interface InstagramClient {


	@PostMapping(value = "/oauth/access_token", consumes = "application/x-www-form-urlencoded")
	Instagram.TokenResponse getAccessToken(
			@RequestBody Map<String, ?> body
	);


}
