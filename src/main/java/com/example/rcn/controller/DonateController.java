package com.example.rcn.controller;

import com.example.rcn.service.ContributionTierService;
import com.example.rcn.service.DonationPageContentService;
import com.example.rcn.service.PaymentDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DonateController {

    private final DonationPageContentService donationPageContentService;
    private final ContributionTierService contributionTierService;
    private final PaymentDetailsService paymentDetailsService;

    public DonateController(DonationPageContentService donationPageContentService,
                           ContributionTierService contributionTierService,
                           PaymentDetailsService paymentDetailsService) {
        this.donationPageContentService = donationPageContentService;
        this.contributionTierService = contributionTierService;
        this.paymentDetailsService = paymentDetailsService;
    }

    @GetMapping("/donate")
    public String donate(Model model) {
        model.addAttribute("donation", donationPageContentService.getSingleton());
        model.addAttribute("tiers", contributionTierService.findAll());
        model.addAttribute("payment", paymentDetailsService.getSingleton());
        return "pages/donate";
    }
}
