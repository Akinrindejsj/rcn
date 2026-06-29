package com.example.rcn.flowable;

import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.service.ArticleService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.time.LocalDateTime;

/**
 * Flowable delegate that fires when an editor approves a submission. Marks the
 * article {@code PUBLISHED} and stamps the publish date.
 */
public class ArticleApprovedDelegate implements JavaDelegate {

    private final ArticleService articleService;

    public ArticleApprovedDelegate(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long articleId = (Long) execution.getVariable("articleId");
        Article article = articleService.findById(articleId)
                .orElseThrow(() -> new IllegalStateException(
                        "Article " + articleId + " no longer exists."));
        article.setStatus(ArticleStatus.PUBLISHED);
        if (article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        articleService.update(article);
    }
}
