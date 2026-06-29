package com.example.rcn.controller;

import com.example.rcn.service.PodcastService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PodcastController {

    private final PodcastService podcastService;

    public PodcastController(PodcastService podcastService) {
        this.podcastService = podcastService;
    }

    @GetMapping("/podcast")
    public String podcast(Model model) {
        model.addAttribute("episodes", podcastService.findAll());
        return "pages/podcast";
    }
}
