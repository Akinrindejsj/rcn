package com.example.rcn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Singleton row of editable About-page content. Same pattern as
 * HomepageContent: one record (id = {@link #SINGLETON_ID}).
 */
@Entity
@Table(name = "about_page_content")
public class AboutPageContent {

    public static final Long SINGLETON_ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String heading;

    @Column(name = "intro_text", columnDefinition = "TEXT")
    private String introText;

    @Column(name = "intro_image_url", length = 1024)
    private String introImageUrl;

    @Column(name = "join_banner_text")
    private String joinBannerText;

    @Column(name = "join_banner_url")
    private String joinBannerUrl;

    protected AboutPageContent() {
    }

    public static AboutPageContent defaults() {
        AboutPageContent c = defaultsTransient();
        c.id = SINGLETON_ID;
        return c;
    }

    public static AboutPageContent defaultsTransient() {
        AboutPageContent c = new AboutPageContent();
        c.heading = "Who We Are";
        c.introText = "The Revolutionary Communists of Nigeria — Nigerian section of the Revolutionary Communist International. "
                + "We fight for a socialist Nigeria, for workers' power, for human liberation.";
        c.introImageUrl = "";
        c.joinBannerText = "Ready? Join the RCN →";
        c.joinBannerUrl = "/join";
        return c;
    }

    // ---- Getters & setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getIntroImageUrl() {
        return introImageUrl;
    }

    public void setIntroImageUrl(String introImageUrl) {
        this.introImageUrl = introImageUrl;
    }

    public String getJoinBannerText() {
        return joinBannerText;
    }

    public void setJoinBannerText(String joinBannerText) {
        this.joinBannerText = joinBannerText;
    }

    public String getJoinBannerUrl() {
        return joinBannerUrl;
    }

    public void setJoinBannerUrl(String joinBannerUrl) {
        this.joinBannerUrl = joinBannerUrl;
    }
}
