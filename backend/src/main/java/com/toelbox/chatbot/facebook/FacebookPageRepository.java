package com.toelbox.chatbot.facebook;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface FacebookPageRepository extends ListCrudRepository<FacebookPage, UUID> {
	Optional<FacebookPage> findByPageId(String pageId);

	@Query("SELECT * FROM facebook_page WHERE page_id = :pageId FOR UPDATE")
	Optional<FacebookPage> findByPageIdForUpdate(@Param("pageId") String pageId);

	List<FacebookPage> findByAgentId(UUID agentId);
}
