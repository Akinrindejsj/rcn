package com.example.rcn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a public request asks for an article that does not exist
 * (or is not published). Resolves to HTTP 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArticleNotFoundException extends RuntimeException {

    public ArticleNotFoundException(Long id) {
        super("Article not found (id=" + id + ")");
    }

    public ArticleNotFoundException(String slug) {
        super("Article not found (slug=" + slug + ")");
    }
}
