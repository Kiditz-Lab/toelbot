package com.toelbox.chatbot.facebook;

import java.util.UUID;

public record FacebookReplyMessageEvent(String pageId, String senderId, String text) {
}
