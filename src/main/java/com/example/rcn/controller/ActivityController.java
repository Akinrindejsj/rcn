package com.example.rcn.controller;

import com.example.rcn.service.ActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/activity")
    public String activity(Model model) {
        model.addAttribute("activities", activityService.findAll());
        return "pages/activity";
    }
}
