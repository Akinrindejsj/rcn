package com.example.rcn.service;

import com.example.rcn.event.SearchIndexRefreshEvent;
import com.example.rcn.model.Faq;
import com.example.rcn.repository.FaqRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FaqService {

    private final FaqRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public FaqService(FaqRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public List<Faq> findAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Optional<Faq> findById(Long id) {
        return repository.findById(id);
    }

    public Faq create(Faq faq) {
        Faq saved = repository.save(faq);
        publishSearchRefresh();
        return saved;
    }

    public Faq update(Faq faq) {
        Faq saved = repository.save(faq);
        publishSearchRefresh();
        return saved;
    }

    public void delete(Long id) {
        repository.deleteById(id);
        publishSearchRefresh();
    }

    private void publishSearchRefresh() {
        eventPublisher.publishEvent(new SearchIndexRefreshEvent());
    }
}
