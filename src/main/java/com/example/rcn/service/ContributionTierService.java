package com.example.rcn.service;

import com.example.rcn.model.ContributionTier;
import com.example.rcn.repository.ContributionTierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContributionTierService {

    private final ContributionTierRepository repository;

    public ContributionTierService(ContributionTierRepository repository) {
        this.repository = repository;
    }

    public List<ContributionTier> findAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Optional<ContributionTier> findById(Long id) {
        return repository.findById(id);
    }

    public ContributionTier create(ContributionTier tier) {
        return repository.save(tier);
    }

    public ContributionTier update(ContributionTier tier) {
        return repository.save(tier);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
