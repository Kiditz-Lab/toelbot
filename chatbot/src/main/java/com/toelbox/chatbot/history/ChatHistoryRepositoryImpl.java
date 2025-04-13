package com.toelbox.chatbot.history;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class ChatHistoryRepositoryImpl implements ChatHistoryRepositoryCustom {

	private final JdbcTemplate jdbcTemplate;


	@Override
	public Page<ChatHistory> findByFilters(UUID agentId, ChatHistoryFilter filter, Pageable pageable) {
		StringBuilder sql = new StringBuilder("SELECT * FROM chat_history WHERE agent_id = ?");
		List<Object> params = new ArrayList<>();
		params.add(agentId);

		// Apply filters (userMessage, startDate, endDate)
		boolean hasMessage = filter.userMessage() != null && !filter.userMessage().isEmpty();
		if (hasMessage) {
			sql.append(" AND user_message LIKE ?");
			params.add("%" + filter.userMessage() + "%");
		}
		if (filter.startDate() != null) {
			sql.append(" AND created_at >= ?");
			params.add(filter.startDate());
		}
		if (filter.endDate() != null) {
			sql.append(" AND created_at <= ?");
			params.add(filter.endDate());
		}

		// Apply sorting from Pageable
		if (pageable.getSort().isSorted()) {
			sql.append(" ORDER BY ");
			pageable.getSort().forEach(order -> {
				String column = camelToSnake(order.getProperty());
				sql.append(column).append(" ").append(order.getDirection()).append(", ");
			});
			sql.setLength(sql.length() - 2); // Remove trailing comma and space
		}


		// Apply pagination (LIMIT and OFFSET)
		int offset = pageable.getPageNumber() * pageable.getPageSize();
		sql.append(" LIMIT ? OFFSET ?");
		params.add(pageable.getPageSize());
		params.add(offset);

		// Fetch paginated data
		List<ChatHistory> content = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(ChatHistory.class), params.toArray());

		// Count query with the same filters
		StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM chat_history WHERE agent_id = ?");
		List<Object> countParams = new ArrayList<>();
		countParams.add(agentId);

		// Add filters to the count query
		if (hasMessage) {
			countSql.append(" AND user_message LIKE ?");
			countParams.add("%" + filter.userMessage() + "%");
		}
		if (filter.startDate() != null) {
			countSql.append(" AND created_at >= ?");
			countParams.add(filter.startDate());
		}
		if (filter.endDate() != null) {
			countSql.append(" AND created_at <= ?");
			countParams.add(filter.endDate());
		}

		int totalCount = Objects.requireNonNull(jdbcTemplate.queryForObject(countSql.toString(), Integer.class, countParams.toArray()));

		return new PageImpl<>(content, pageable, totalCount);
	}

	private String camelToSnake(String input) {
		return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
	}


}