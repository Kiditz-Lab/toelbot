package com.toelbox.chatbot.agent;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("model")
@Data
@Builder
class Model {
	@Id
	private UUID id;
	private String name;
	private ModelVendor vendor;
	private String model;
}
