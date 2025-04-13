package com.toelbox.chatbot.history;

import java.time.LocalDateTime;

public record ChatHistoryFilter(String userMessage, LocalDateTime startDate, LocalDateTime endDate) {
}
