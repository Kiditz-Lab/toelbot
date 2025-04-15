package com.toelbox.chatbot.facebook;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facebook")
@RequiredArgsConstructor
@Tag(name = "Facebook")
class FacebookController {

	private final FacebookService facebookService;

	@GetMapping("/callback")
	ResponseEntity<List<Facebook.Page>> handleCallback(@RequestParam String code) {
		Facebook.TokenResponse token = facebookService.exchangeCodeForAccessToken(code);
		List<Facebook.Page> pages = facebookService.getUserPages(token.getAccess_token());
		return ResponseEntity.ok(pages);
	}

	@PostMapping("/api/v1/save-page")
	ResponseEntity<Void> savePage(@RequestBody Facebook.SavePageRequest req) {
		// Save to DB (req.getPageId(), req.getAccessToken(), etc.)
		facebookService.subscribePage(req.getAccessToken());
		return ResponseEntity.ok().build();
	}
}
