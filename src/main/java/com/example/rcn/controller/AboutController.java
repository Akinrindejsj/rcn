package com.example.rcn.controller;

import com.example.rcn.service.AboutPageContentService;
import com.example.rcn.service.FaqService;
import com.example.rcn.service.TeamMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    private final AboutPageContentService aboutPageContentService;
    private final TeamMemberService teamMemberService;
    private final FaqService faqService;

    public AboutController(AboutPageContentService aboutPageContentService,
                           TeamMemberService teamMemberService,
                           FaqService faqService) {
        this.aboutPageContentService = aboutPageContentService;
        this.teamMemberService = teamMemberService;
        this.faqService = faqService;
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("about", aboutPageContentService.getSingleton());
        model.addAttribute("programmePoints", teamMemberService.findAll());
        model.addAttribute("faqs", faqService.findAll());
        return "pages/about";
    }
}
