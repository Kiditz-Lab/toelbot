package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.IpAddress;
import com.toelbox.chatbot.history.IpApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Tag(name = "Agents")
@Slf4j
class AgentChatController {
	private final AgentChatService service;
	private final AgentQueryService queryService;
	private final IpApiService ipApiService;


	@PostMapping("/{id}/chats")
	Flux<String> chat(@PathVariable UUID id, @Valid @RequestBody AgentChat chat, HttpServletRequest request, Principal principal) throws Exception {
		Agent agent = queryService.findById(id);
		if (principal == null && !agent.isPublic()) {
			throw new AccessDeniedException("Your agent is not public");
		}
		String ipAddress = IpAddress.getClientIp(request);
		String countryCode = "";
		try {
			countryCode = ipApiService.getIpInfo(ipAddress).getCountryCode();
		} catch (Exception ignored) {
		}
		return service.chat(agent, chat, countryCode);
	}

	@GetMapping("/models")
	List<Map<String, String>> getModels() {
		return service.aiModels();
	}

}
