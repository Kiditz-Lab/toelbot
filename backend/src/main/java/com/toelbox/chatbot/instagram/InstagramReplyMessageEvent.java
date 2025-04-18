package com.toelbox.chatbot.instagram;

public record InstagramReplyMessageEvent(String recipientId, String senderId, String text) {
}
