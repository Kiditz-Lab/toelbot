package com.toelbox.chatbot.rag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface TrainingDataRepository extends CrudRepository<TrainingData, UUID>, PagingAndSortingRepository<TrainingData, UUID>, TrainingDataRepositoryCustom {
	Optional<TrainingData> findByFile(String file);

	@Query("SELECT COUNT(id) FROM training_data WHERE agent_id = :agentId")
	long countByAgentId(UUID agentId);

	@Query("SELECT COALESCE(SUM(size), 0) FROM training_data WHERE agent_id = :agentId AND type = :type")
	long sumSizeByAgentIdAndType(UUID agentId, TrainingType type);


	Optional<TrainingData> findByAgentIdAndType(UUID agentId, TrainingType type);

	List<TrainingData> getByAgentIdAndTypeAndStatus(UUID agentId, TrainingType type, TrainingStatus status);

}
