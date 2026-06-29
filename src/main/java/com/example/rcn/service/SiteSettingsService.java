package com.example.rcn.service;

import com.example.rcn.model.SiteSettings;
import com.example.rcn.repository.SiteSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SiteSettingsService {

    private static final Long SINGLETON_ID = SiteSettings.SINGLETON_ID;

    private final SiteSettingsRepository repository;

    public SiteSettingsService(SiteSettingsRepository repository) {
        this.repository = repository;
    }

    public SiteSettings getSingleton() {
        return repository.findById(SINGLETON_ID)
                .orElseGet(() -> repository.save(SiteSettings.defaultsTransient()));
    }

    public SiteSettings save(SiteSettings settings) {
        return repository.save(settings);
    }
}
