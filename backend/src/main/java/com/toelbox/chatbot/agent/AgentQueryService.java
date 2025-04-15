package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class AgentQueryService {
	private final AgentRepository repository;

	List<Agent> getAgents(Principal principal) {
		return repository.findByCreatedBy(principal.getName());
	}

	Agent findById(UUID id) throws AccessDeniedException {
		return repository.findById(id).orElseThrow(() -> new NotFoundException("Agent not found with ID: " + id));
	}
}
