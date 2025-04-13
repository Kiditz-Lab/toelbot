package com.toelbox.chatbot.tools;

import java.util.UUID;

record ToolListResponse(UUID id, String imageUrl,String name, String shortDescription, String description) {
}
