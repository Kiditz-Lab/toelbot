package com.toelbox.chatbot.core;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Timing {
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
}
