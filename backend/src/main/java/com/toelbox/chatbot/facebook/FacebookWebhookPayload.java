package com.toelbox.chatbot.facebook;

import java.util.List;

record FacebookWebhookPayload(List<Entry> entry) {

	record Entry(String id, long time, List<Messaging> messaging) {
	}

	record Messaging(Sender sender, Message message) {
	}

	record Sender(String id) {
	}

	record Message(String text) {
	}
}
