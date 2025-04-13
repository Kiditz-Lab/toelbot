package com.toelbox.chatbot.agent;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Tag(name = "Agents")
public class AgentQueryController {
	private final AgentQueryService service;

	@GetMapping
	List<Agent> getAgents(Principal principal) {
		return service.getAgents(principal);
	}

	@GetMapping("/{id}")
	Agent getAgent(@PathVariable UUID id) {
		return service.findById(id);
	}
}
