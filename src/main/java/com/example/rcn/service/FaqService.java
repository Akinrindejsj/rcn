package com.example.rcn.service;

import com.example.rcn.model.Faq;
import com.example.rcn.repository.FaqRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FaqService {

    private final FaqRepository repository;

    public FaqService(FaqRepository repository) {
        this.repository = repository;
    }

    public List<Faq> findAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Optional<Faq> findById(Long id) {
        return repository.findById(id);
    }

    public Faq create(Faq faq) {
        return repository.save(faq);
    }

    public Faq update(Faq faq) {
        return repository.save(faq);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
