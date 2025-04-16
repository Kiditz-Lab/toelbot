package com.toelbox.chatbot.instagram;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "instagramClient", url = "https://graph.facebook.com/v22.0")
interface InstagramClient {

	@GetMapping("/oauth/access_token")
	Instagram.TokenResponse getAccessToken(
			@RequestParam("client_id") String clientId,
			@RequestParam("redirect_uri") String redirectUri,
			@RequestParam("client_secret") String clientSecret,
			@RequestParam("code") String code
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
