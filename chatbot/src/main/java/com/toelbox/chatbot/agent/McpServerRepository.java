package com.toelbox.chatbot.agent;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface McpServerRepository extends ListCrudRepository<McpServer, UUID> {
	List<McpServer> findByAgentId(UUID agentId);
	Optional<McpServer> findByAgentIdAndToolsId(UUID agentId, UUID toolsId);
}
