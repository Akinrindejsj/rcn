package com.example.rcn.repository;

import com.example.rcn.model.ContributionTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionTierRepository extends JpaRepository<ContributionTier, Long> {

    List<ContributionTier> findAllByOrderBySortOrderAsc();
}
