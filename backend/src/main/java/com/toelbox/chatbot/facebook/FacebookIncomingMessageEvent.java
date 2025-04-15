package com.toelbox.chatbot.facebook;

import java.util.UUID;

public record FacebookIncomingMessageEvent(UUID agentId, String pageId, String senderId, String text) {
}
