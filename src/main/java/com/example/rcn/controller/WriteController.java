package com.example.rcn.controller;

import com.example.rcn.dto.WriteArticleCmd;
import com.example.rcn.exception.CloudinaryUploadException;
import com.example.rcn.model.Activity;
import com.example.rcn.model.ActivityStatus;
import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.model.ArticleType;
import com.example.rcn.service.ActivityService;
import com.example.rcn.service.ArticleService;
import com.example.rcn.service.CloudinaryService;
import com.example.rcn.service.MediaService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/write")
public class WriteController {

    private final ArticleService articleService;
    private final ActivityService activityService;
    private final CloudinaryService cloudinaryService;
    private final MediaService mediaService;
    private final RuntimeService runtimeService;

    public WriteController(ArticleService articleService,
                           ActivityService activityService,
                           CloudinaryService cloudinaryService,
                           MediaService mediaService,
                           RuntimeService runtimeService) {
        this.articleService = articleService;
        this.activityService = activityService;
        this.cloudinaryService = cloudinaryService;
        this.mediaService = mediaService;
        this.runtimeService = runtimeService;
    }

    @GetMapping
    public String write(Model model) {
        if (!model.containsAttribute("cmd")) {
            model.addAttribute("cmd", new WriteArticleCmd());
        }
        return "pages/write";
    }

    /**
     * Accepts a visitor submission (article or activity), persists it as a draft,
     * then routes it to the appropriate approval queue.
     */
    @PostMapping
    public String submit(@ModelAttribute("cmd") WriteArticleCmd cmd,
                         RedirectAttributes redirectAttributes) {
        try {
            validate(cmd);

            // Check submission type and route accordingly
            if ("activity".equalsIgnoreCase(cmd.getSubmissionType())) {
                // Route to activity queue
                handleActivitySubmission(cmd, redirectAttributes);
            } else {
                // Route to article queue (default)
                handleArticleSubmission(cmd, redirectAttributes);
            }

            return "redirect:/write";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("cmd", cmd);
            return "redirect:/write";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            redirectAttributes.addFlashAttribute("cmd", cmd);
            return "redirect:/write";
        }
    }

    /**
     * Handles article submissions: creates an Article, sets PENDING_APPROVAL status,
     * and kicks off the Flowable article-approval workflow.
     */
    private void handleArticleSubmission(WriteArticleCmd cmd, RedirectAttributes redirectAttributes) throws CloudinaryUploadException {
        Article article = new Article();
        article.setTitle(cmd.getTitle().trim());
        article.setBody(cmd.getBody().trim());
        article.setCategory(cmd.getCategory());
        article.setAuthorName(cmd.getAuthorName() == null ? "Anonymous Comrade" : cmd.getAuthorName().trim());
        article.setArticleType(parseArticleType(cmd.getCategory()));
        article.setStatus(ArticleStatus.DRAFT);

        if (cmd.getAttachment() != null && !cmd.getAttachment().isEmpty()) {
            String url = cloudinaryService.uploadImage(cmd.getAttachment(), "rcn/submissions");
            article.setFeaturedImageUrl(url);
            mediaService.upload(cmd.getAttachment(), "rcn/submissions", article.getAuthorName());
        }

        article = articleService.create(article);

        // Route the article to the editorial approval queue
        article.setStatus(ArticleStatus.PENDING_APPROVAL);
        articleService.update(article);

        // Best-effort: kick off the Flowable "article-approval" workflow
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("articleId", article.getId());
            variables.put("articleTitle", article.getTitle());
            variables.put("submitterEmail", cmd.getEmailAddress());
            variables.put("approved", false);
            ProcessInstance process = runtimeService.startProcessInstanceByKey(
                    "article-approval", variables);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thanks! Your article has been sent to our editors for review. "
                            + "Process id: " + process.getId());
        } catch (Exception flowableEx) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thanks! Your article has been sent to our editors for review.");
        }
    }

    /**
     * Handles activity submissions: creates an Activity, sets PENDING_APPROVAL status,
     * and saves it for CMS admin review.
     */
    private void handleActivitySubmission(WriteArticleCmd cmd, RedirectAttributes redirectAttributes) throws CloudinaryUploadException {
        Activity activity = new Activity();
        activity.setTitle(cmd.getTitle().trim());
        activity.setBody(cmd.getBody().trim());
        activity.setLocation(cmd.getActivityLocation());
        activity.setType(cmd.getActivityType());
        activity.setAuthorName(cmd.getAuthorName() == null ? "Anonymous Comrade" : cmd.getAuthorName().trim());
        activity.setApprovalStatus(ActivityStatus.DRAFT);

        if (cmd.getAttachment() != null && !cmd.getAttachment().isEmpty()) {
            String url = cloudinaryService.uploadImage(cmd.getAttachment(), "rcn/submissions");
            activity.setImageUrl(url);
            mediaService.upload(cmd.getAttachment(), "rcn/submissions", activity.getAuthorName());
        }

        activity = activityService.create(activity);

        // Route the activity to the CMS approval queue
        activity.setApprovalStatus(ActivityStatus.PENDING_APPROVAL);
        activityService.update(activity);

        // Activities are reviewed in the CMS by admin staff
        // The submission is now in the database and visible in the CMS panel
        redirectAttributes.addFlashAttribute("successMessage",
                "Thanks! Your activity report has been submitted for review. "
                        + "The RCN team will review it and feature it soon.");
    }

    private void validate(WriteArticleCmd cmd) {
        if (cmd.getTitle() == null || cmd.getTitle().isBlank()) {
            throw new IllegalArgumentException("Please give your submission a title.");
        }
        if (cmd.getBody() == null || cmd.getBody().isBlank()) {
            throw new IllegalArgumentException("Please write the body of your submission.");
        }
        if (cmd.getEmailAddress() == null || cmd.getEmailAddress().isBlank()) {
            throw new IllegalArgumentException("Please provide an email address so our editors can contact you.");
        }
        if (cmd.getAttachment() != null && !cmd.getAttachment().isEmpty()) {
            if (cmd.getAttachment().getSize() > 5L * 1024 * 1024) {
                throw new IllegalArgumentException("Attachment is too large (max 5 MB). Please choose a smaller file.");
            }
        }
    }

    private static ArticleType parseArticleType(String category) {
        if (category == null) {
            return ArticleType.OTHER;
        }
        return switch (category) {
            case "Frontline Report" -> ArticleType.FRONTLINE_REPORT;
            case "Letter & Debate" -> ArticleType.LETTER;
            default -> ArticleType.ANALYSIS;
        };
    }
}
