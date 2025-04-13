package com.toelbox.chatbot.tools;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

interface ToolsRepository extends ListCrudRepository<Tools, UUID> {
//	List<Tools> findAllByNameContainingIgnoreCase(String name, Sort sort);

	List<ToolListResponse> findAllByNameContainingIgnoreCase(String name, Sort sort);
}
