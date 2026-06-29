package com.example.rcn.service;

import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.repository.ArticleRepository;
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

    public ArticleService(ArticleRepository repository) {
        this.repository = repository;
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
        return repository.save(article);
    }

    public Article update(Article article) {
        if (article.getTitle() != null && (article.getSlug() == null || article.getSlug().isBlank())) {
            article.setSlug(slugify(article.getTitle()));
        }
        return repository.save(article);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Article publish(Long id) {
        Article a = repository.findById(id).orElseThrow();
        a.setStatus(ArticleStatus.PUBLISHED);
        if (a.getPublishedAt() == null) {
            a.setPublishedAt(LocalDateTime.now());
        }
        return repository.save(a);
    }

    public Article approve(Long id) {
        Article a = repository.findById(id).orElseThrow();
        a.setStatus(ArticleStatus.APPROVED);
        return repository.save(a);
    }

    public Article reject(Long id) {
        Article a = repository.findById(id).orElseThrow();
        a.setStatus(ArticleStatus.REJECTED);
        return repository.save(a);
    }

    public Article submitForApproval(Long id) {
        Article a = repository.findById(id).orElseThrow();
        a.setStatus(ArticleStatus.PENDING_APPROVAL);
        return repository.save(a);
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
}
