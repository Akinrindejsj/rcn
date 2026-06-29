package com.example.rcn.service;

import com.example.rcn.model.DonationPageContent;
import com.example.rcn.repository.DonationPageContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DonationPageContentService {

    private static final Long SINGLETON_ID = DonationPageContent.SINGLETON_ID;

    private final DonationPageContentRepository repository;

    public DonationPageContentService(DonationPageContentRepository repository) {
        this.repository = repository;
    }

    public DonationPageContent getSingleton() {
        return repository.findById(SINGLETON_ID)
                .orElseGet(() -> repository.save(DonationPageContent.defaultsTransient()));
    }

    public DonationPageContent save(DonationPageContent content) {
        return repository.save(content);
    }
}
