package com.toelbox.chatbot.agent;

record ModelCommand(
    String name,
    ModelVendor vendor,
    String model
) {
}
