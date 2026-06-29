package com.example.rcn.service;

import com.example.rcn.model.Podcast;
import com.example.rcn.repository.PodcastRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PodcastService {

    private final PodcastRepository repository;

    public PodcastService(PodcastRepository repository) {
        this.repository = repository;
    }

    public List<Podcast> findAll() {
        return repository.findAllByOrderByPublishedAtDesc();
    }

    public Optional<Podcast> findById(Long id) {
        return repository.findById(id);
    }

    public Podcast create(Podcast podcast) {
        return repository.save(podcast);
    }

    public Podcast update(Podcast podcast) {
        return repository.save(podcast);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Podcast> findByIdsIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return repository.findByIdIn(ids);
    }
}
