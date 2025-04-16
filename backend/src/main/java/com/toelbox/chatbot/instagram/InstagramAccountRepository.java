package com.toelbox.chatbot.instagram;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

interface InstagramAccountRepository extends ListCrudRepository<InstagramAccount, String> {
	List<InstagramAccount> findByAgentId(UUID agentId);
}