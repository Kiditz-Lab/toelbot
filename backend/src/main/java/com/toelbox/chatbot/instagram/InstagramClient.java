package com.toelbox.chatbot.instagram;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "instagramClient", url = "https://api.instagram.com", configuration = FeignConfig.class)
interface InstagramClient {

	@PostMapping("/oauth/access_token")
	@Headers("Content-Type: application/x-www-form-urlencoded")
	Instagram.TokenResponse getAccessToken(
			@RequestBody Map<String, ?> body
	);

	@GetMapping("/me/accounts")
	Instagram.FacebookAccountsResponse getFacebookPages(
			@RequestParam("access_token") String accessToken
	);

	@GetMapping("/{page-id}")
	Instagram.PageDetails getPageDetails(
			@PathVariable("page-id") String pageId,
			@RequestParam("fields") String fields,
			@RequestParam("access_token") String accessToken
	);

	@GetMapping("/{ig-id}")
	Instagram.IgBusinessProfile getInstagramAccountDetails(
			@PathVariable("ig-id") String instagramBusinessId,
			@RequestParam("fields") String fields,
			@RequestParam("access_token") String accessToken
	);
}
