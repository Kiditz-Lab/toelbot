package com.toelbox.chatbot.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("chat_history")
public class ChatHistory {
    
    @Id
    private UUID id;
    private UUID agentId;
    private String chatId;
    private String botMessage;
    private String userMessage;
    private String countryCode;
    private String country;
    private String model;
    private LocalDateTime createdAt;
}