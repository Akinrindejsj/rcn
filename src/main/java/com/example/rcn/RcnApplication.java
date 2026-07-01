package com.example.rcn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.rcn.model.MembersVoice;
import com.example.rcn.service.MembersVoiceService;

import java.util.List;

@SpringBootApplication
public class RcnApplication {

    public static void main(String[] args) {
        SpringApplication.run(RcnApplication.class, args);
    }

    @Bean
    public org.springframework.boot.CommandLineRunner seedMembers(MembersVoiceService membersVoiceService) {
        return args -> {
            List<MembersVoice> existing = membersVoiceService.findAll();
            if (existing == null || existing.isEmpty()) {
                MembersVoice m1 = new MembersVoice();
                m1.setAuthorName("Amaka T");
                m1.setLocation("Lagos");
                m1.setQuote("I joined because I was tired of waiting for politicians to save us. The RCN showed me that we are the ones we've been waiting for.");
                m1.setSortOrder(1);
                membersVoiceService.create(m1);

                MembersVoice m2 = new MembersVoice();
                m2.setAuthorName("Emeka O");
                m2.setLocation("Abuja");
                m2.setQuote("I never understood why ASUU strikes kept failing until I studied Marxism. Now I organise with purpose.");
                m2.setSortOrder(2);
                membersVoiceService.create(m2);

                MembersVoice m3 = new MembersVoice();
                m3.setAuthorName("Blessing A");
                m3.setLocation("Port Harcourt");
                m3.setQuote("In the Niger Delta, we see the oil wealth every day but live in poverty. The RCN gives me a way to fight back.");
                m3.setSortOrder(3);
                membersVoiceService.create(m3);
            }
        };
    }

}
