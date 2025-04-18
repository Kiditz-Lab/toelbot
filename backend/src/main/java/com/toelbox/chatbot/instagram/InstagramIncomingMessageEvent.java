package com.toelbox.chatbot.instagram;

import java.util.UUID;

public record InstagramIncomingMessageEvent(UUID agentId, String recipientId, String senderId, String text) {
}
