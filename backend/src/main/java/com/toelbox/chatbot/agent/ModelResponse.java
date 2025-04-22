package com.toelbox.chatbot.agent;

import java.util.UUID;

record ModelResponse(
    UUID id,
    String name,
    ModelVendor vendor,
    String model
) {
}
