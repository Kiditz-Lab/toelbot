package com.toelbox.chatbot.model;

import com.toelbox.chatbot.core.ModelVendor;

record ModelCommand(
    String name,
    ModelVendor vendor,
    String model
) {
}
