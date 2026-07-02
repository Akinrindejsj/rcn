package com.example.rcn.service;

import com.example.rcn.event.SearchIndexRefreshEvent;
import com.example.rcn.model.AboutPageContent;
import com.example.rcn.repository.AboutPageContentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AboutPageContentService {

    private static final Long SINGLETON_ID = AboutPageContent.SINGLETON_ID;

    private final AboutPageContentRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public AboutPageContentService(AboutPageContentRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public AboutPageContent getSingleton() {
        return repository.findById(SINGLETON_ID)
                .orElseGet(() -> repository.save(AboutPageContent.defaultsTransient()));
    }

    public AboutPageContent save(AboutPageContent content) {
        AboutPageContent saved = repository.save(content);
        eventPublisher.publishEvent(new SearchIndexRefreshEvent());
        return saved;
    }
}
