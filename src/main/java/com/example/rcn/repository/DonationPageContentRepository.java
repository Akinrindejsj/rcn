package com.example.rcn.repository;

import com.example.rcn.model.DonationPageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationPageContentRepository extends JpaRepository<DonationPageContent, Long> {
}
