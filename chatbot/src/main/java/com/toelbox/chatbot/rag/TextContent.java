package com.toelbox.chatbot.rag;

import jakarta.validation.constraints.NotEmpty;

record TextContent(@NotEmpty String content) {
}
