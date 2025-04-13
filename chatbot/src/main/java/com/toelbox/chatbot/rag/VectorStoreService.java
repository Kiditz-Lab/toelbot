package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
class VectorStoreService {
	private final VectorStore vectorStore;

	void delete(String fileKey, String fileName, UUID agentId) {
		var template = (JdbcTemplate) vectorStore.getNativeClient().orElse(null);
		String sql = "DELETE FROM vector_store WHERE metadata->>'%s' = ? AND metadata->>'agentId' = ?".formatted(fileKey);
		assert template != null;
		template.update(sql, fileName, agentId.toString());
	}
}
