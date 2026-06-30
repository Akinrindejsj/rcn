package com.example.rcn.config;

import com.example.rcn.model.ArticleStatus;
import com.example.rcn.service.ArticleService;
import com.example.rcn.service.SiteSettingsService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Injects the singleton {@code settings} (SiteSettings) and the
 * {@code pendingApprovalCount} into every request's model so the shared
 * layout (footer, sidebar badge) can render dynamically.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final SiteSettingsService siteSettingsService;
    private final ArticleService articleService;

    public GlobalModelAdvice(SiteSettingsService siteSettingsService,
                             ArticleService articleService) {
        this.siteSettingsService = siteSettingsService;
        this.articleService = articleService;
    }

    @ModelAttribute("settings")
    public com.example.rcn.model.SiteSettings settings() {
        return siteSettingsService.getSingleton();
    }

    @ModelAttribute("pendingApprovalCount")
    public long pendingApprovalCount() {
        return articleService.countByStatus(ArticleStatus.PENDING_APPROVAL);
    }
}
