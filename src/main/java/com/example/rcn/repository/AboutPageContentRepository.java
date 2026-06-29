package com.example.rcn.repository;

import com.example.rcn.model.AboutPageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutPageContentRepository extends JpaRepository<AboutPageContent, Long> {
}
