package com.toelbox.chatbot.instagram;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "instagramClient", url = "https://graph.instagram.com/v22.0", configuration = FeignConfig.class)
interface InstagramClient {

	@GetMapping("/me")
	Instagram.Account getMe(
			@RequestParam("fields") String fields,
			@RequestParam("access_token") String accessToken
	);
}
