package com.toelbox.chatbot.facebook;

public record FacebookReplyMessageEvent(String pageId, String senderId, String text) {
}
