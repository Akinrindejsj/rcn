package com.example.rcn.controller;

import com.example.rcn.dto.WriteArticleCmd;
import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.model.ArticleType;
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
    private final CloudinaryService cloudinaryService;
    private final MediaService mediaService;
    private final RuntimeService runtimeService;

    public WriteController(ArticleService articleService,
                           CloudinaryService cloudinaryService,
                           MediaService mediaService,
                           RuntimeService runtimeService) {
        this.articleService = articleService;
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
     * Accepts a visitor submission, persists it as a draft, then kicks off the
     * Flowable "article-approval" process so editors can review it.
     */
    @PostMapping
    public String submit(@ModelAttribute("cmd") WriteArticleCmd cmd,
                         RedirectAttributes redirectAttributes) {
        try {
            validate(cmd);

            Article article = new Article();
            article.setTitle(cmd.getTitle().trim());
            article.setBody(cmd.getBody().trim());
            article.setCategory(cmd.getCategory());
            article.setAuthorName(cmd.getAuthorName() == null ? "Anonymous Comrade" : cmd.getAuthorName().trim());
            article.setArticleType(parseType(cmd.getCategory()));
            article.setStatus(ArticleStatus.DRAFT);

            if (cmd.getAttachment() != null && !cmd.getAttachment().isEmpty()) {
                String url = cloudinaryService.uploadImage(cmd.getAttachment(), "rcn/submissions");
                article.setFeaturedImageUrl(url);
                mediaService.upload(cmd.getAttachment(), "rcn/submissions", article.getAuthorName());
            }

            article = articleService.create(article);

            // Route the article to the editorial approval queue directly. This is
            // the source of truth for where editors find submissions, so it must
            // happen regardless of whether the optional Flowable workflow below
            // succeeds.
            article.setStatus(ArticleStatus.PENDING_APPROVAL);
            articleService.update(article);

            // Best-effort: kick off the Flowable "article-approval" workflow so
            // editors get a task in their workflow inbox. A failure here must
            // NEVER surface to the visitor or block the submission — the article
            // is already safely in the approval queue above.
            try {
                Map<String, Object> variables = new HashMap<>();
                variables.put("articleId", article.getId());
                variables.put("articleTitle", article.getTitle());
                variables.put("submitterEmail", cmd.getEmailAddress());
                variables.put("approved", false);
                ProcessInstance process = runtimeService.startProcessInstanceByKey(
                        "article-approval", variables);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Thanks! Your submission has been sent to our editors for review. "
                                + "Process id: " + process.getId());
            } catch (Exception flowableEx) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Thanks! Your submission has been sent to our editors for review.");
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

    private static ArticleType parseType(String category) {
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
