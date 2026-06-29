package com.example.rcn.controller;

import com.example.rcn.exception.ArticleNotFoundException;
import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Public-facing article browsing: index, category, and individual article pages.
 * The CMS admin controllers live in CmsController.
 */
@Controller
public class ArticleListController {

    private final ArticleService articleService;

    private static final int DEFAULT_PAGE_SIZE = 12;

    public ArticleListController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * GET /news — browse all published articles.
     * Optional: ?category=Analysis&page=0&size=12
     */
    @GetMapping("/news")
    public String news(@RequestParam(value = "category", required = false) String category,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "12") int size,
                       Model model) {
        int safePage = Math.max(0, page);
        int safeSize = (size < 1 || size > 50) ? DEFAULT_PAGE_SIZE : size;

        List<Article> articles;
        int totalPages;
        if (category != null && !category.isBlank()) {
            // Category view is un-paged (the result set is small); the service
            // filters by status + category in the query.
            articles = articleService.findByStatusAndCategory(ArticleStatus.PUBLISHED, category);
            totalPages = 1;
        } else {
            Page<Article> articlePage = articleService.findPublishedPageable(safePage, safeSize);
            articles = articlePage.getContent();
            totalPages = articlePage.getTotalPages();
        }

        model.addAttribute("articles", articles);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("categories", articleService.publishedCategories());
        model.addAttribute("selectedCategory", category);
        return "pages/news";
    }

    /**
     * GET /article/{id} — read a single published article by numeric id.
     */
    @GetMapping("/article/{id}")
    public String articleById(@PathVariable("id") Long id, Model model) {
        Article article = articleService.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        model.addAttribute("article", article);
        return "pages/article";
    }

    /**
     * GET /article/slug/{slug} — read a published article by slug.
     */
    @GetMapping("/article/slug/{slug}")
    public String articleBySlug(@PathVariable("slug") String slug, Model model) {
        Article article = articleService.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));
        model.addAttribute("article", article);
        return "pages/article";
    }
}
