package com.example.rcn.config;

import com.example.rcn.model.ContributionTier;
import com.example.rcn.repository.ContributionTierRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * On startup insert the base contribution tiers if none exist.
 */
@Component
public class DataInitializer {

    private final ContributionTierRepository tierRepository;

    public DataInitializer(ContributionTierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    @PostConstruct
    @Transactional
    public void ensureBaseTiers() {
        if (tierRepository.count() > 0) {
            return;
        }

        List<ContributionTier> defaults = new ArrayList<>();

        ContributionTier t1 = new ContributionTier();
        t1.setAmount("₦5,000");
        t1.setTierName("Sustainer");
        t1.setDescription("Monthly contribution to sustain the press and organising work.");
        t1.setRecurring(true);
        t1.setSortOrder(1);

        ContributionTier t2 = new ContributionTier();
        t2.setAmount("₦10,000");
        t2.setTierName("Builder");
        t2.setDescription("Support organising and outreach activities.");
        t2.setRecurring(true);
        t2.setSortOrder(2);

        ContributionTier t3 = new ContributionTier();
        t3.setAmount("₦25,000");
        t3.setTierName("Organiser");
        t3.setDescription("Help fund publications, books and schools.");
        t3.setRecurring(false);
        t3.setSortOrder(3);

        ContributionTier t4 = new ContributionTier();
        t4.setAmount("₦50,000");
        t4.setTierName("Revolutionary");
        t4.setDescription("Major contributions for national work and solidarity.");
        t4.setRecurring(false);
        t4.setSortOrder(4);

        defaults.add(t1);
        defaults.add(t2);
        defaults.add(t3);
        defaults.add(t4);

        tierRepository.saveAll(defaults);
    }
}

