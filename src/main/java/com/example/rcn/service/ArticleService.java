package com.example.rcn.service;

import com.example.rcn.event.SearchIndexRefreshEvent;
import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.repository.ArticleRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArticleService {

    private final ArticleRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ArticleService(ArticleRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public List<Article> findAll() {
        return repository.findAll();
    }

    public List<Article> findPublished() {
        return repository.findByStatusOrderByPublishedAtDesc(ArticleStatus.PUBLISHED);
    }

    public Page<Article> findPublishedPageable(int page, int size) {
        return repository.findByStatusOrderByPublishedAtDesc(ArticleStatus.PUBLISHED,
                PageRequest.of(page, size));
    }

    public List<Article> findByStatus(ArticleStatus status) {
        return repository.findByStatusOrderByPublishedAtDesc(status);
    }

    public List<Article> findByStatusAndCategory(ArticleStatus status, String category) {
        if (category == null || category.isBlank()) {
            return findByStatus(status);
        }
        return repository.findByStatusAndCategoryOrderByPublishedAtDesc(status, category);
    }

    public Optional<Article> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Article> findBySlug(String slug) {
        return repository.findBySlug(slug);
    }

    public List<String> publishedCategories() {
        return repository.findDistinctPublishedCategories();
    }

    public long countByStatus(ArticleStatus status) {
        return repository.countByStatus(status);
    }

    public Article create(Article article) {
        article.setStatus(ArticleStatus.DRAFT);
        if (article.getTitle() != null && (article.getSlug() == null || article.getSlug().isBlank())) {
            article.setSlug(slugify(article.getTitle()));
        }
        Article saved = repository.save(article);
        publishSearchRefresh();
        return saved;
    }

    public Article update(Article article) {
        if (article.getTitle() != null && (article.getSlug() == null || article.getSlug().isBlank())) {
            article.setSlug(slugify(article.getTitle()));
        }
        Article saved = repository.save(article);
        publishSearchRefresh();
        return saved;
    }

    public void delete(Long id) {
        repository.deleteById(id);
        publishSearchRefresh();
    }

    public Article publish(Long id) {
        Article a = repository.findById(id).orElseThrow();
        a.setStatus(ArticleStatus.PUBLISHED);
        if (a.getPublishedAt() == null) {
            a.setPublishedAt(LocalDateTime.now());
        }
        Article saved = repository.save(a);
        publishSearchRefresh();
        return saved;
    }

    public Article approve(Long id) {
        Article a = repository.findById(id).orElseThrow();
        a.setStatus(ArticleStatus.APPROVED);
        Article saved = repository.save(a);
        publishSearchRefresh();
        return saved;
    }

    public Article reject(Long id) {
        Article a = repository.findById(id).orElseThrow();
        a.setStatus(ArticleStatus.REJECTED);
        Article saved = repository.save(a);
        publishSearchRefresh();
        return saved;
    }

    public Article submitForApproval(Long id) {
        Article a = repository.findById(id).orElseThrow();
        a.setStatus(ArticleStatus.PENDING_APPROVAL);
        Article saved = repository.save(a);
        publishSearchRefresh();
        return saved;
    }

    public List<Article> findByIdsIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return repository.findByIdIn(ids);
    }

    private static String slugify(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    private void publishSearchRefresh() {
        eventPublisher.publishEvent(new SearchIndexRefreshEvent());
    }
}
