package com.toelbox.chatbot.agent;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

interface AgentRepository extends ListCrudRepository<Agent, UUID> {
	List<Agent> findByCreatedBy(String createdBy);

	boolean existsByAgentKey(String agentKey);
}
