package com.toelbox.chatbot.history;

import com.querydsl.core.annotations.QueryEntity;
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
@QueryEntity
@Table("chat_history")
public class ChatHistory {
    
    @Id
    private UUID id;
    private UUID agentId;
    private String chatId;
    private String botMessage;
    private String userMessage;
    private String ipAddress;
    private String countryCode;
    private String country;
    private String regionName;
    private String regionCode;
    private Double latitude;
    private Double longitude;
    private String model;
    private LocalDateTime createdAt;
}