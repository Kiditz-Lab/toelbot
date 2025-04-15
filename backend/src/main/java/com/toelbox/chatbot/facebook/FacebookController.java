package com.toelbox.chatbot.facebook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facebook")
@RequiredArgsConstructor
@Tag(name = "Facebook")
@Slf4j
class FacebookController {
	private final ObjectMapper mapper;
	private final FacebookService facebookService;
	private final FacebookConfigProp prop;

	@GetMapping("/callback")
	public ResponseEntity<String> handleCallback(@RequestParam String code) throws JsonProcessingException {
		try {
			Facebook.TokenResponse token = facebookService.exchangeCodeForAccessToken(code);
			List<Facebook.Page> pages = facebookService.getUserPages(token.getAccess_token());
			String json = mapper.writeValueAsString(pages);
//			String escapedJson = json.replace("\\", "\\\\").replace("\"", "\\\"");

			log.info("Target Origin : {}", prop.getTargetOrigin());
			String html = """
					<!DOCTYPE html>
					<html>
					<body>
					<script>
					    const pages = %s;
					    window.opener.postMessage({
					        type: "facebook-connected",
					        payload: {
					            status: "success",
					            pages: pages
					        }
					    }, "%s");
					    setTimeout(() => window.close(), 500);
					</script>
					</body>
					</html>
					""".formatted(json, prop.getTargetOrigin());

			return ResponseEntity.ok()
					.contentType(MediaType.TEXT_HTML)
					.body(html);
		} catch (Exception e) {
			log.error("Unexpected error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(generateErrorHtml("An unexpected error occurred", prop.getTargetOrigin()));

		}
	}

	private String generateErrorHtml(String message, String targetOrigin) {
		return String.format("""
				<!DOCTYPE html>
				<html>
				<body>
				<script>
				    window.opener.postMessage({
				        type: "facebook-connected",
				        payload: {
				            status: "error",
				            message: "%s"
				        }
				    }, "%s");
				    setTimeout(() => window.close(), 500);
				</script>
				</body>
				</html>
				""", message, targetOrigin);
	}

	@PostMapping("/api/v1/save-page")
	ResponseEntity<Void> savePage(@RequestBody Facebook.SavePageRequest req) {
		// Save to DB (req.getPageId(), req.getAccessToken(), etc.)
		facebookService.subscribePage(req.getAccessToken());
		return ResponseEntity.ok().build();
	}
}
