package com.toelbox.chatbot.rag;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

class TrainingDataRowMapper implements RowMapper<TrainingData> {
	@Override
	public TrainingData mapRow(ResultSet rs, int rowNum) throws SQLException {
		LocalDateTime createdAt = null;
		Timestamp timestamp = rs.getTimestamp("created_at");
		if (timestamp != null) {
			createdAt = timestamp.toLocalDateTime();
		}

		return TrainingData.builder()
				.id(UUID.fromString(rs.getString("id")))
				.prefix(rs.getString("prefix"))
				.mimeType(rs.getString("mime_type"))
				.file(rs.getString("file"))
				.agentId(UUID.fromString(rs.getString("agent_id")))
				.content(rs.getString("content"))
				.url(rs.getString("url"))
				.size(rs.getLong("size"))
				.status(TrainingStatus.valueOf(rs.getString("status")))
				.type(TrainingType.valueOf(rs.getString("type")))
				.progress(rs.getBoolean("progress"))
				.website(mapEmbeddedWebsite(rs)) // 👈 embedded
				.createdBy(rs.getString("created_by"))
				.createdAt(createdAt)
				.build();
	}

	private TrainingWebsite mapEmbeddedWebsite(ResultSet rs) throws SQLException {
		String title = rs.getString("title");
		if (title == null) return null;
		return new TrainingWebsite(
				title,
				rs.getString("description"),
				rs.getString("image")
		);
	}

}
