package com.example.rcn.service;

import com.example.rcn.event.SearchIndexRefreshEvent;
import com.example.rcn.model.TeamMember;
import com.example.rcn.repository.TeamMemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamMemberService {

    private final TeamMemberRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public TeamMemberService(TeamMemberRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public List<TeamMember> findAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Optional<TeamMember> findById(Long id) {
        return repository.findById(id);
    }

    public TeamMember create(TeamMember member) {
        TeamMember saved = repository.save(member);
        publishSearchRefresh();
        return saved;
    }

    public TeamMember update(TeamMember member) {
        TeamMember saved = repository.save(member);
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
