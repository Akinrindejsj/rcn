package com.example.rcn.controller;

import com.example.rcn.service.ActivityPageContentService;
import com.example.rcn.service.ActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityPageContentService activityPageContentService;

    public ActivityController(ActivityService activityService,
                              ActivityPageContentService activityPageContentService) {
        this.activityService = activityService;
        this.activityPageContentService = activityPageContentService;
    }

    @GetMapping("/activity")
    public String activity(Model model) {
        model.addAttribute("activitySettings", activityPageContentService.getSingleton());
        model.addAttribute("activities", activityService.findAll());
        return "pages/activity";
    }
}
