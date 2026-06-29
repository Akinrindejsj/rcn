package com.example.rcn.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * CMS admin panel. Every method returns a Thymeleaf view name under templates/cms/,
 * rendered inside the shared cms_layout.html (sidebar + header).
 */
@Controller
@RequestMapping("/admin/cms")
public class CmsController {

    /**
     * Exposes the current request URI to every CMS view as ${currentUrl}.
     * Used by the shared sidebar to highlight the active page.
     *
     * Note: we inject jakarta.servlet.http.HttpServletRequest here rather than
     * relying on Thymeleaf's #request expression object, which is no longer
     * available by default in Thymeleaf 3.1+.
     */
    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String index() {
        return "redirect:/admin/cms/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "cms/dashboard";
    }

    // Content
    @GetMapping("/articles")
    public String articles() {
        return "cms/cms_articles";
    }

    @GetMapping("/articles/new")
    public String article() {
        return "cms/cms_article_form";
    }

    @GetMapping("/articles/{id}")
    public String articleEdit(@PathVariable String id) {
        return "cms/cms_article_form";
    }

    @GetMapping("/approval")
    public String articleApproval() {
        return "cms/cms_article_approval";
    }

    @GetMapping("/events")
    public String events() {
        return "cms/cms_events";
    }

    @GetMapping("/events/new")
    public String eventForm() {
        return "cms/cms_event_form";
    }

    // Site pages
    @GetMapping("/homepage")
    public String homepage() {
        return "cms/cms_homepage";
    }

    @GetMapping("/about")
    public String about() {
        return "cms/cms_about";
    }

    @GetMapping("/donations")
    public String donations() {
        return "cms/cms_donations";
    }

    @GetMapping("/podcast")
    public String podcast() {
        return "cms/cms_podcast";
    }

    // Assets & people
    @GetMapping("/images")
    public String images() {
        return "cms/cms_images";
    }

    @GetMapping("/media")
    public String media() {
        return "cms/cms_media";
    }

    @GetMapping("/users")
    public String users() {
        return "cms/cms_users";
    }

    @GetMapping("/settings")
    public String settings() {
        return "cms/cms_settings";
    }
}
