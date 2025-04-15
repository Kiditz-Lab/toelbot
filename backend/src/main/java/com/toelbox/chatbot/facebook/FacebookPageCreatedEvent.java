package com.toelbox.chatbot.facebook;

import java.util.UUID;

public record FacebookPageCreatedEvent(String pageId, UUID agentId) {
}
