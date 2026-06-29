package com.example.rcn.service;

import com.example.rcn.model.AboutPageContent;
import com.example.rcn.repository.AboutPageContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AboutPageContentService {

    private static final Long SINGLETON_ID = AboutPageContent.SINGLETON_ID;

    private final AboutPageContentRepository repository;

    public AboutPageContentService(AboutPageContentRepository repository) {
        this.repository = repository;
    }

    public AboutPageContent getSingleton() {
        return repository.findById(SINGLETON_ID)
                .orElseGet(() -> repository.save(AboutPageContent.defaultsTransient()));
    }

    public AboutPageContent save(AboutPageContent content) {
        return repository.save(content);
    }
}
