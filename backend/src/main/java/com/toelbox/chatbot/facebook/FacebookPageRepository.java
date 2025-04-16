package com.toelbox.chatbot.facebook;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.repository.Lock;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface FacebookPageRepository extends ListCrudRepository<FacebookPage, UUID> {
	Optional<FacebookPage> findByPageId(String pageId);

	@Lock(LockMode.PESSIMISTIC_WRITE)
	@Query("SELECT f FROM FacebookPage f WHERE f.pageId = :pageId")
	Optional<FacebookPage> findByPageIdForUpdate(@Param("pageId") String pageId);

	List<FacebookPage> findByAgentId(UUID agentId);
}
