package com.example.rcn.controller;

import com.example.rcn.exception.CloudinaryUploadException;
import com.example.rcn.service.HomepageContentService;
import jakarta.servlet.http.HttpServletRequest;

import com.example.rcn.model.HomepageContent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * CMS admin panel. Every method returns a Thymeleaf view name under templates/cms/,
 * rendered inside the shared cms_layout.html (sidebar + header).
 */
@Controller
@RequestMapping("/admin/cms")
public class CmsController {

    private final HomepageContentService homepageContentService;

    public CmsController(HomepageContentService homepageContentService) {
        this.homepageContentService = homepageContentService;
    }

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

    /**
     * GET /admin/cms/homepage — renders the homepage editor pre-filled with
     * the current singleton content row.
     */
    @GetMapping("/homepage")
    public String homepage(Model model) {
        HomepageContent content = homepageContentService.getSingleton();
        model.addAttribute("content", content);
        return "cms/cms_homepage";
    }

    /**
     * POST /admin/cms/homepage — persists text + image edits to the singleton
     * row and redirects back with a flash success/error message.
     */
    @PostMapping("/homepage")
    public String homepageSave(@ModelAttribute("content") com.example.rcn.dto.HomepageContentUpdateDto dto,
                                @RequestParam Map<String, MultipartFile> images,
                                RedirectAttributes redirectAttributes) {
        try {
            homepageContentService.update(dto, images);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Homepage updated successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Image upload failed: " + e.getMessage()
                            + " Please try again or use a different file.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong. Please try again.");
        }
        return "redirect:/admin/cms/homepage";
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
