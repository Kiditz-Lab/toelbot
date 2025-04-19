package com.toelbox.auth_client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(OAuth2AuthenticationToken authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String registrationId = authentication.getAuthorizedClientRegistrationId(); // "google-drive" or "google-calendar"
        OAuth2User user = authentication.getPrincipal();

        String sub = user.getAttribute("sub"); // Unique user ID (Google's subject ID)
        String email = user.getAttribute("email");

        model.addAttribute("registrationId", registrationId);
        model.addAttribute("email", email);
        model.addAttribute("sub", sub);

        return "index";
    }

}
