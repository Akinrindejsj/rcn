package com.example.rcn.repository;

import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByStatusOrderByPublishedAtDesc(ArticleStatus status);

    List<Article> findByStatusAndCategoryOrderByPublishedAtDesc(ArticleStatus status, String category);

    List<Article> findByIdIn(List<Long> ids);

    Page<Article> findByStatusOrderByPublishedAtDesc(ArticleStatus status, Pageable pageable);

    long countByStatus(ArticleStatus status);

    Optional<Article> findBySlug(String slug);

    @Query("select distinct a.category from Article a where a.status = 'PUBLISHED' order by a.category")
    List<String> findDistinctPublishedCategories();
}
