package com.toelbox.chatbot.facebook;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facebook")
@RequiredArgsConstructor
public class FacebookController {

	private final FacebookService facebookService;

	@GetMapping("/callback")
	ResponseEntity<List<Facebook.Page>> handleCallback(@RequestParam String code) {
		Facebook.TokenResponse token = facebookService.exchangeCodeForAccessToken(code);
		List<Facebook.Page> pages = facebookService.getUserPages(token.getAccess_token());
		return ResponseEntity.ok(pages);
	}

	@PostMapping("/save-page")
	ResponseEntity<Void> savePage(@RequestBody Facebook.SavePageRequest req) {
		// Save to DB (req.getPageId(), req.getAccessToken(), etc.)
		facebookService.subscribePage(req.getAccessToken());
		return ResponseEntity.ok().build();
	}
}
