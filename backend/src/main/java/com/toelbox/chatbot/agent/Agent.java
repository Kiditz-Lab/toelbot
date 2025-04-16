package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.Timing;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "agent")
public class Agent {

	@Id
	private UUID id;

	private String name;
	@Column("is_public")
	private boolean isPublic;

	private String agentKey;

	@Embedded.Empty
	private AgentConfig config;

	@Embedded.Empty
	private Timing timing;

	@Version
	private Integer version;

	@CreatedBy
	private String createdBy;
	private List<String> tools;
//	private List<String> facebooks;
}
