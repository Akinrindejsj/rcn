package com.example.rcn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Singleton row of editable Donation-page content.
 */
@Entity
@Table(name = "donation_page_content")
public class DonationPageContent {

    public static final Long SINGLETON_ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_heading")
    private String pageHeading;

    @Column(name = "intro_text", columnDefinition = "TEXT")
    private String introText;

    @Column(name = "footer_quote", columnDefinition = "TEXT")
    private String footerQuote;

    private String footnote;

    @Column(name = "printing_press_image_url", length = 1024)
    private String printingPressImageUrl;

    @Column(name = "donation_confirmation_email")
    private String donationConfirmationEmail;

    protected DonationPageContent() {
    }

    public static DonationPageContent defaults() {
        DonationPageContent d = defaultsTransient();
        d.id = SINGLETON_ID;
        return d;
    }

    public static DonationPageContent defaultsTransient() {
        DonationPageContent d = new DonationPageContent();
        d.pageHeading = "Fund the Revolution";
        d.introText = "Every naira builds the revolutionary press, funds organising work, "
                + "and trains the next generation of Nigerian communists.";
        d.footerQuote = "The liberation of the working class must be the act of the working class itself.";
        d.footnote = "— Marx, General Rules of the International Working Men's Association";
        d.printingPressImageUrl = "";
        d.donationConfirmationEmail = "donations@rcn.ng";
        return d;
    }

    // ---- Getters & setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPageHeading() {
        return pageHeading;
    }

    public void setPageHeading(String pageHeading) {
        this.pageHeading = pageHeading;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getFooterQuote() {
        return footerQuote;
    }

    public void setFooterQuote(String footerQuote) {
        this.footerQuote = footerQuote;
    }

    public String getFootnote() {
        return footnote;
    }

    public void setFootnote(String footnote) {
        this.footnote = footnote;
    }

    public String getPrintingPressImageUrl() {
        return printingPressImageUrl;
    }

    public void setPrintingPressImageUrl(String printingPressImageUrl) {
        this.printingPressImageUrl = printingPressImageUrl;
    }

    public String getDonationConfirmationEmail() {
        return donationConfirmationEmail;
    }

    public void setDonationConfirmationEmail(String donationConfirmationEmail) {
        this.donationConfirmationEmail = donationConfirmationEmail;
    }
}
