package com.example.rcn.controller;

import com.example.rcn.dto.JoinCmd;
import com.example.rcn.service.EmailService;
import com.example.rcn.service.MembersVoiceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles the public "Join the RCN" membership form(s). Both the dedicated
 * /join page and the dashboard "Get in Touch" form POST here. On success the
 * visitor's details are emailed to the configured join-notification recipient
 * and a success toast is flashed (rendered by the shared layout).
 */
@Controller
public class JoinController {

    private final EmailService emailService;
    private final MembersVoiceService membersVoiceService;

    public JoinController(EmailService emailService, MembersVoiceService membersVoiceService) {
        this.emailService = emailService;
        this.membersVoiceService = membersVoiceService;
    }

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("members", membersVoiceService.findAll());
        model.addAttribute("cmd", new JoinCmd());
        return "pages/join";
    }

    @PostMapping("/join")
    public String submit(@Valid @ModelAttribute("cmd") JoinCmd cmd,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Please complete all required fields before submitting.");
            return "redirect:/join";
        }

        try {
            emailService.sendJoinNotification(cmd);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Welcome, comrade! A comrade will contact you within 48 hours.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "We couldn't send your application just now. Please try again in a moment.");
        }
        return "redirect:/join";
    }
}
