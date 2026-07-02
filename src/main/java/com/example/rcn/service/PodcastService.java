package com.example.rcn.service;

import com.example.rcn.event.SearchIndexRefreshEvent;
import com.example.rcn.model.Podcast;
import com.example.rcn.repository.PodcastRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PodcastService {

    private final PodcastRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public PodcastService(PodcastRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public List<Podcast> findAll() {
        return repository.findAllByOrderByPublishedAtDesc();
    }

    public Optional<Podcast> findById(Long id) {
        return repository.findById(id);
    }

    public Podcast create(Podcast podcast) {
        Podcast saved = repository.save(podcast);
        publishSearchRefresh();
        return saved;
    }

    public Podcast update(Podcast podcast) {
        Podcast saved = repository.save(podcast);
        publishSearchRefresh();
        return saved;
    }

    public void delete(Long id) {
        repository.deleteById(id);
        publishSearchRefresh();
    }

    public List<Podcast> findByIdsIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return repository.findByIdIn(ids);
    }

    private void publishSearchRefresh() {
        eventPublisher.publishEvent(new SearchIndexRefreshEvent());
    }
}
