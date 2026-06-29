package com.example.rcn.service;

import com.example.rcn.model.TeamMember;
import com.example.rcn.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamMemberService {

    private final TeamMemberRepository repository;

    public TeamMemberService(TeamMemberRepository repository) {
        this.repository = repository;
    }

    public List<TeamMember> findAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Optional<TeamMember> findById(Long id) {
        return repository.findById(id);
    }

    public TeamMember create(TeamMember member) {
        return repository.save(member);
    }

    public TeamMember update(TeamMember member) {
        return repository.save(member);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
