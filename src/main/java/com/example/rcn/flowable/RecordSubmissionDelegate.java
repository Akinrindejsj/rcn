package com.example.rcn.flowable;

import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.service.ArticleService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Flowable delegate that fires when a submitted article enters the approval
 * process. Loads the article (by id captured as process variable
 * {@code articleId}) and sets its status to {@code PENDING_APPROVAL}.
 */
public class RecordSubmissionDelegate implements JavaDelegate {

    private final ArticleService articleService;

    public RecordSubmissionDelegate(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Long articleId = (Long) execution.getVariable("articleId");
        Article article = articleService.findById(articleId)
                .orElseThrow(() -> new IllegalStateException(
                        "Article " + articleId + " no longer exists."));
        article.setStatus(ArticleStatus.PENDING_APPROVAL);
        articleService.update(article);
    }
}
