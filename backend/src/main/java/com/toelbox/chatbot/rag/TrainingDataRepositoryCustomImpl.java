package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class TrainingDataRepositoryCustomImpl implements TrainingDataRepositoryCustom {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public Page<TrainingData> findByAgentIdAndTypeWithSearch(UUID agentId, TrainingType type, String search, @Nullable Pageable pageable) {
		String sortClause = buildSortClause(Objects.requireNonNull(pageable).getSort());
		List<TrainingData> data = findByAgentIdTypeAndSearch(agentId, type.name(), search, pageable.getPageNumber(), pageable.getPageSize(), sortClause);
		int total = countByAgentIdTypeAndSearch(agentId, type.name(), search);

		return new PageImpl<>(data, pageable, total);
	}

	List<TrainingData> findByAgentIdTypeAndSearch(UUID agentId, String type, String search, int page, int size, String sortClause) {
		String sql = String.format("""
				    SELECT * FROM training_data
				    WHERE agent_id = ?
				      AND type = ?
				      AND (title ILIKE ? OR url ILIKE ?)
				    %s
				    LIMIT ? OFFSET ?
				""", sortClause);


		return jdbcTemplate.query(sql, new TrainingDataRowMapper(), agentId, type, "%" + search + "%", "%" + search + "%", size, page * size

		);
	}


	Integer countByAgentIdTypeAndSearch(UUID agentId, String type, String search) {
		String sql = """
				    SELECT COUNT(*) FROM training_data
				    WHERE agent_id = ?
				      AND type = ?
				      AND (title ILIKE ? OR url ILIKE ?)
				""";

		return jdbcTemplate.queryForObject(sql, Integer.class, agentId, type, "%" + search + "%", "%" + search + "%");
	}

	private String buildSortClause(Sort sort) {
		if (sort == null || sort.isUnsorted()) {
			return "ORDER BY created_at DESC";
		}

		String clause = sort.stream()
				.map(order -> camelToSnake(order.getProperty()) + " " + order.getDirection().name())
				.collect(Collectors.joining(", "));

		return "ORDER BY " + clause;
	}


	private String camelToSnake(String input) {
		return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
	}


}
