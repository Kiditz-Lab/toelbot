package com.toelbox.chatbot.agent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

record McpServerResponse(
    UUID id,
    String serverName,
    String command,
    List<String> args,
    Map<String, String> env
) {}
