package com.example.rcn.config;

import com.example.rcn.model.Faq;
import com.example.rcn.model.TeamMember;
import com.example.rcn.repository.FaqRepository;
import com.example.rcn.repository.TeamMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the database with the default About-page programme points and FAQs
 * when the relevant tables are empty. Idempotent — safe to run on every
 * startup; won't duplicate or overwrite user edits.
 */
@Component
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final TeamMemberRepository teamMemberRepository;
    private final FaqRepository faqRepository;

    public DataSeeder(TeamMemberRepository teamMemberRepository,
                      FaqRepository faqRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.faqRepository = faqRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        seedProgrammePoints();
        seedFaqs();
    }

    private void seedProgrammePoints() {
        if (teamMemberRepository.count() > 0) {
            return;
        }
        log.info("Seeding default programme points …");
        String[][] points = {
                {"01", "For a Workers' Government", "The state must serve the majority who produce all wealth."},
                {"02", "Nationalise Oil", "Nigeria's oil belongs to Nigerians, not Shell or Chevron."},
                {"03", "Land to the Peasants", "End land grabbing; give land to those who work it."},
                {"04", "End Police Brutality", "Disband SARS; build community-controlled safety."},
                {"05", "Free Education & Healthcare", "Human needs before private profit."},
                {"06", "Socialist Federation of Africa", "Unity against imperialism and capitalism."},
        };
        for (int i = 0; i < points.length; i++) {
            TeamMember m = new TeamMember();
            m.setOrderNumber(points[i][0]);
            m.setPointTitle(points[i][1]);
            m.setPointDescription(points[i][2]);
            m.setSortOrder(i + 1);
            teamMemberRepository.save(m);
        }
    }

    private void seedFaqs() {
        if (faqRepository.count() > 0) {
            return;
        }
        log.info("Seeding default FAQs …");
        String[][] faqs = {
                {"Is the RCN a political party?", "We are building a revolutionary party. We do not participate in bourgeois elections that serve the ruling class."},
                {"Can I join if I'm religious?", "Yes. We are a political organisation, not a religious one. Many comrades are people of faith."},
                {"Is it dangerous?", "The Nigerian state represses dissent. We take security seriously. But the greatest danger is doing nothing while the country burns."},
                {"How is the RCN funded?", "Entirely by workers' contributions. We take no corporate, NGO, or government money."},
        };
        for (int i = 0; i < faqs.length; i++) {
            Faq f = new Faq();
            f.setQuestion(faqs[i][0]);
            f.setAnswer(faqs[i][1]);
            f.setSortOrder(i + 1);
            faqRepository.save(f);
        }
    }
}
