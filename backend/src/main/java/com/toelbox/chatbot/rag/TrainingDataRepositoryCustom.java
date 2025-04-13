package com.toelbox.chatbot.rag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

interface TrainingDataRepositoryCustom {
	Page<TrainingData> findByAgentIdAndTypeWithSearch(UUID agentId, TrainingType type, String search, Pageable pageable);
}
