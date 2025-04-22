package com.toelbox.chatbot.model;

import com.toelbox.chatbot.core.ModelVendor;

import java.util.UUID;

record ModelResponse(
    UUID id,
    String name,
    ModelVendor vendor,
    String model
) {
}
