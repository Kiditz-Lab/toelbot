package com.toelbox.chatbot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebConfig {

    // Handle client-side paths and forward them to index.html
    @RequestMapping(value = "/{path:^(?!api\\/v[0-9]+|static).*}")
    public String forwardToIndex() {
        return "forward:/index.html";
    }

    // Catch-all for deeper paths, excluding API and static resources
    @RequestMapping(value = "/**/{path:^(?!api\\/v[0-9]+|static).*}")
    public String forwardToIndex2() {
        return "forward:/index.html";
    }
}
