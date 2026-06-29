package com.example.rcn.config;

import com.example.rcn.service.SiteSettingsService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Injects the singleton {@code settings} (SiteSettings) into every request's model
 * so the shared layout footer (banner text, social links, copyright) can render
 * dynamically. Keeps every controller free of footer boilerplate.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final SiteSettingsService siteSettingsService;

    public GlobalModelAdvice(SiteSettingsService siteSettingsService) {
        this.siteSettingsService = siteSettingsService;
    }

    @ModelAttribute("settings")
    public com.example.rcn.model.SiteSettings settings() {
        return siteSettingsService.getSingleton();
    }
}
