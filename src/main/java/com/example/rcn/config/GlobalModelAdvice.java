package com.example.rcn.config;

import com.example.rcn.model.ArticleStatus;
import com.example.rcn.service.ArticleService;
import com.example.rcn.service.SiteSettingsService;
import com.example.rcn.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Injects the singleton {@code settings} (SiteSettings), the
 * {@code pendingApprovalCount}, and user authentication information
 * into every request's model so the shared layout (footer, sidebar badge,
 * user profile) can render dynamically.
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

    /**
     * Provides the authenticated user's full name from Keycloak.
     */
    @ModelAttribute("currentUserName")
    public String currentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Object name = oidcUser.getClaims().get("name");
            return name != null ? name.toString() : "User";
        }
        return "User";
    }

    /**
     * Provides the authenticated user's email from Keycloak.
     */
    @ModelAttribute("currentUserEmail")
    public String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Object email = oidcUser.getClaims().get("email");
            return email != null ? email.toString() : "CMS Editor";
        }
        return "CMS Editor";
    }

    /**
     * Provides the authenticated user's initials for the avatar.
     */
    @ModelAttribute("currentUserInitials")
    public String currentUserInitials() {
        String userName = currentUserName();
        return StringUtils.getInitials(userName);
    }
}
