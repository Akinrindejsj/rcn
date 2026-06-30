package com.example.rcn.service;

import com.example.rcn.model.ActivityPageContent;
import com.example.rcn.repository.ActivityPageContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivityPageContentService {

    private static final Long SINGLETON_ID = ActivityPageContent.SINGLETON_ID;

    private final ActivityPageContentRepository repository;

    public ActivityPageContentService(ActivityPageContentRepository repository) {
        this.repository = repository;
    }

    public ActivityPageContent getSingleton() {
        return repository.findById(SINGLETON_ID)
                .orElseGet(() -> repository.save(ActivityPageContent.defaultsTransient()));
    }

    public ActivityPageContent save(ActivityPageContent content) {
        return repository.save(content);
    }
}
