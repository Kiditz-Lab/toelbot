package com.toelbox.chatbot.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "training_data")
public class TrainingData {
	@Id
	private UUID id;
	private String prefix;
	private String mimeType;
	private String file;
	private UUID agentId;
	private String content;
	private String url;
	private long size;
	private TrainingStatus status;
	private TrainingType type;
	private boolean progress;
	@Embedded.Nullable
	private TrainingWebsite website;
	@CreatedBy
	private String createdBy;
	private LocalDateTime createdAt;

}
