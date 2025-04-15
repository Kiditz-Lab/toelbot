package com.toelbox.chatbot.facebook;

import java.util.UUID;

public record FacebookPageRemovedEvent(String pageId, UUID agentId) {
}
