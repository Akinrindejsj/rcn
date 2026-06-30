package com.example.rcn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Singleton row of editable Activity-page content. Same pattern as
 * {@link AboutPageContent}: one record (id = {@link #SINGLETON_ID}) holds every
 * piece of page-level copy on the visitor Activity page — hero, stat strip,
 * map strip, and footer CTA. The report cards themselves are managed as their
 * own {@link Activity} records.
 */
@Entity
@Table(name = "activity_page_content")
public class ActivityPageContent {

    public static final Long SINGLETON_ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Hero ----
    @Column(name = "page_title")
    private String pageTitle;

    private String kicker;

    private String headline;

    @Column(name = "intro_text", columnDefinition = "TEXT")
    private String introText;

    @Column(name = "backdrop_image_url", length = 1024)
    private String backdropImageUrl;

    @Column(name = "cta1_text")
    private String cta1Text;

    @Column(name = "cta1_url")
    private String cta1Url;

    @Column(name = "cta2_text")
    private String cta2Text;

    @Column(name = "cta2_url")
    private String cta2Url;

    // ---- Stat strip ----
    @Column(name = "stat1_number")
    private String stat1Number;

    @Column(name = "stat1_label")
    private String stat1Label;

    @Column(name = "stat2_number")
    private String stat2Number;

    @Column(name = "stat2_label")
    private String stat2Label;

    @Column(name = "stat3_number")
    private String stat3Number;

    @Column(name = "stat3_label")
    private String stat3Label;

    // ---- Map strip ----
    @Column(name = "map_heading")
    private String mapHeading;

    @Column(name = "map_body_text", columnDefinition = "TEXT")
    private String mapBodyText;

    @Column(name = "map_image_url", length = 1024)
    private String mapImageUrl;

    // ---- Footer CTA ----
    @Column(name = "footer_heading")
    private String footerHeading;

    @Column(name = "footer_body_text", columnDefinition = "TEXT")
    private String footerBodyText;

    @Column(name = "footer_cta_text")
    private String footerCtaText;

    @Column(name = "footer_cta_url")
    private String footerCtaUrl;

    protected ActivityPageContent() {
    }

    public static ActivityPageContent defaults() {
        ActivityPageContent c = defaultsTransient();
        c.id = SINGLETON_ID;
        return c;
    }

    public static ActivityPageContent defaultsTransient() {
        ActivityPageContent c = new ActivityPageContent();
        // Hero
        c.pageTitle = "Activity";
        c.kicker = "From the Frontlines";
        c.headline = "We Are Already\nOrganising.";
        c.introText = "This isn't theory on a page — it's comrades on Lagos buses, students in Abuja lecture halls, "
                + "and oil workers in Port Harcourt, organising right now. Every report below is a real cell, doing real work. "
                + "The only thing missing is you.";
        c.backdropImageUrl = "";
        c.cta1Text = "See the Reports ↓";
        c.cta1Url = "#reports";
        c.cta2Text = "Join a Cell Near You →";
        c.cta2Url = "/join";
        // Stats
        c.stat1Number = "40+";
        c.stat1Label = "Active cells nationwide";
        c.stat2Number = "120+";
        c.stat2Label = "Frontline reports filed";
        c.stat3Number = "9";
        c.stat3Label = "Major cities organising";
        // Map strip
        c.mapHeading = "Wherever Workers Are, We're Building.";
        c.mapBodyText = "From danfo terminals in Lagos to oil rigs in the Delta, RCN cells are rooted in the places "
                + "where Nigerian working life actually happens.";
        c.mapImageUrl = "";
        // Footer CTA
        c.footerHeading = "Your City Needs a Cell.\nBe the First to Build It.";
        c.footerBodyText = "Every cell on this page started with one person who decided to stop waiting. That person could be you.";
        c.footerCtaText = "Join the RCN →";
        c.footerCtaUrl = "/join";
        return c;
    }

    // ---- Getters & setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getKicker() {
        return kicker;
    }

    public void setKicker(String kicker) {
        this.kicker = kicker;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getBackdropImageUrl() {
        return backdropImageUrl;
    }

    public void setBackdropImageUrl(String backdropImageUrl) {
        this.backdropImageUrl = backdropImageUrl;
    }

    public String getCta1Text() {
        return cta1Text;
    }

    public void setCta1Text(String cta1Text) {
        this.cta1Text = cta1Text;
    }

    public String getCta1Url() {
        return cta1Url;
    }

    public void setCta1Url(String cta1Url) {
        this.cta1Url = cta1Url;
    }

    public String getCta2Text() {
        return cta2Text;
    }

    public void setCta2Text(String cta2Text) {
        this.cta2Text = cta2Text;
    }

    public String getCta2Url() {
        return cta2Url;
    }

    public void setCta2Url(String cta2Url) {
        this.cta2Url = cta2Url;
    }

    public String getStat1Number() {
        return stat1Number;
    }

    public void setStat1Number(String stat1Number) {
        this.stat1Number = stat1Number;
    }

    public String getStat1Label() {
        return stat1Label;
    }

    public void setStat1Label(String stat1Label) {
        this.stat1Label = stat1Label;
    }

    public String getStat2Number() {
        return stat2Number;
    }

    public void setStat2Number(String stat2Number) {
        this.stat2Number = stat2Number;
    }

    public String getStat2Label() {
        return stat2Label;
    }

    public void setStat2Label(String stat2Label) {
        this.stat2Label = stat2Label;
    }

    public String getStat3Number() {
        return stat3Number;
    }

    public void setStat3Number(String stat3Number) {
        this.stat3Number = stat3Number;
    }

    public String getStat3Label() {
        return stat3Label;
    }

    public void setStat3Label(String stat3Label) {
        this.stat3Label = stat3Label;
    }

    public String getMapHeading() {
        return mapHeading;
    }

    public void setMapHeading(String mapHeading) {
        this.mapHeading = mapHeading;
    }

    public String getMapBodyText() {
        return mapBodyText;
    }

    public void setMapBodyText(String mapBodyText) {
        this.mapBodyText = mapBodyText;
    }

    public String getMapImageUrl() {
        return mapImageUrl;
    }

    public void setMapImageUrl(String mapImageUrl) {
        this.mapImageUrl = mapImageUrl;
    }

    public String getFooterHeading() {
        return footerHeading;
    }

    public void setFooterHeading(String footerHeading) {
        this.footerHeading = footerHeading;
    }

    public String getFooterBodyText() {
        return footerBodyText;
    }

    public void setFooterBodyText(String footerBodyText) {
        this.footerBodyText = footerBodyText;
    }

    public String getFooterCtaText() {
        return footerCtaText;
    }

    public void setFooterCtaText(String footerCtaText) {
        this.footerCtaText = footerCtaText;
    }

    public String getFooterCtaUrl() {
        return footerCtaUrl;
    }

    public void setFooterCtaUrl(String footerCtaUrl) {
        this.footerCtaUrl = footerCtaUrl;
    }
}
