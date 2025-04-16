package com.toelbox.chatbot.facebook;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface FacebookPageRepository extends ListCrudRepository<FacebookPage, UUID> {
	Optional<FacebookPage> findByPageId(String pageId);

	List<FacebookPage> findByAgentId(UUID agentId);
}
