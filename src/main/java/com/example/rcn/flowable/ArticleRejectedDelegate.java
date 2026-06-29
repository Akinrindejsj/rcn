package com.example.rcn.flowable;

import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.service.ArticleService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Flowable delegate that fires when an editor rejects a submission. Marks the
 * article {@code REJECTED} so the author can revise and resubmit.
 */
public class ArticleRejectedDelegate implements JavaDelegate {

    private final ArticleService articleService;

    public ArticleRejectedDelegate(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long articleId = (Long) execution.getVariable("articleId");
        Article article = articleService.findById(articleId)
                .orElseThrow(() -> new IllegalStateException(
                        "Article " + articleId + " no longer exists."));
        article.setStatus(ArticleStatus.REJECTED);
        articleService.update(article);
    }
}
