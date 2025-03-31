package org.mlesyk.ui.controller;

import org.mlesyk.ui.security.EVEOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private static String portraitURL = "https://images.evetech.net/characters/%s/portrait?tenant=tranquility&size=256";

    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal EVEOAuth2User user, Model model) {
        if (user != null) {
            model.addAttribute("user_portrait", String.format(portraitURL, user.getCharacterId()));
        }
        return "profile";
    }
}