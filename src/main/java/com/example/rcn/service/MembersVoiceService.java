package com.example.rcn.service;

import com.example.rcn.model.MembersVoice;
import com.example.rcn.repository.MembersVoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MembersVoiceService {

    private final MembersVoiceRepository repository;

    public MembersVoiceService(MembersVoiceRepository repository) {
        this.repository = repository;
    }

    public List<MembersVoice> findAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Optional<MembersVoice> findById(Long id) {
        return repository.findById(id);
    }

    public MembersVoice create(MembersVoice voice) {
        return repository.save(voice);
    }

    public MembersVoice update(MembersVoice voice) {
        return repository.save(voice);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

