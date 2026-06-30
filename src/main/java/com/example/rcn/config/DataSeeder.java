package com.example.rcn.config;

import com.example.rcn.model.Activity;
import com.example.rcn.model.Faq;
import com.example.rcn.model.TeamMember;
import com.example.rcn.repository.ActivityRepository;
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
    private final ActivityRepository activityRepository;

    public DataSeeder(TeamMemberRepository teamMemberRepository,
                      FaqRepository faqRepository,
                      ActivityRepository activityRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.faqRepository = faqRepository;
        this.activityRepository = activityRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        seedProgrammePoints();
        seedFaqs();
        seedActivities();
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

    private void seedActivities() {
        if (activityRepository.count() > 0) {
            return;
        }
        log.info("Seeding default activity reports …");
        Object[][] reports = {
                {"80 Papers Gone in One Hour at Ojota", "Lagos", "Street Tabling", "Amaka T.",
                        "2026-06-12", "Workers took every copy; one danfo driver called it \"the truth nobody says.\""},
                {"UniAbuja Marxist Study Group Formed", "Abuja", "Study Circle", "Emeka O.",
                        "2026-05-21", "Seven students joined after our ASUU briefing; now meeting every Thursday."},
                {"Oil Workers Speak Out at NUPENG Rally", "Port Harcourt", "Rally", "Blessing A.",
                        "2026-04-18", "A worker denounced gas flaring and poverty amid the Delta's oil wealth. The crowd roared."},
                {"First RCN Cell Launches in the North", "Kano", "Campus", "Yusuf I.",
                        "2026-04-09", "Bayero University students founded Kano's first cell after a screening of \"From #EndSARS to Revolution.\""},
                {"\"Why Communism?\" Draws 60 to Town Hall", "Ibadan", "Public Meeting", "Funmi A.",
                        "2026-03-28", "Our biggest public meeting yet — standing room only by the final question."},
                {"Comrades March Against Fuel Price Hikes", "Enugu", "Protest", "Chidi N.",
                        "2026-02-14", "RCN banners led the chant as transport workers joined the route through the city centre."},
        };
        for (Object[] r : reports) {
            Activity a = new Activity();
            a.setTitle((String) r[0]);
            a.setLocation((String) r[1]);
            a.setType((String) r[2]);
            a.setAuthorName((String) r[3]);
            a.setActivityDate(java.time.LocalDate.parse((String) r[4]));
            a.setBody((String) r[5]);
            activityRepository.save(a);
        }
    }
}
