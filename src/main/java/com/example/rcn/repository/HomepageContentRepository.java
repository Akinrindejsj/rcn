package com.example.rcn.repository;

import com.example.rcn.model.HomepageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomepageContentRepository extends JpaRepository<HomepageContent, Long> {
    // No custom queries needed — the singleton row is loaded by its constant id.
}
