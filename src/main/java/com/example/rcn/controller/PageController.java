package com.example.rcn.controller;

import com.example.rcn.model.HomepageContent;
import com.example.rcn.service.HomepageContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final HomepageContentService homepageContentService;

    public PageController(HomepageContentService homepageContentService) {
        this.homepageContentService = homepageContentService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        HomepageContent content = homepageContentService.getSingleton();
        model.addAttribute("content", content);
        return "pages/dashboard";
    }

    @GetMapping("/theory")
    public String theory() {
        return "pages/theory";
    }

    @GetMapping("/join")
    public String join() {
        return "pages/join";
    }

    @GetMapping("/404")
    public String notFound() {
        return "pages/404";
    }
}
