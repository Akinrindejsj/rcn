package com.example.rcn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Singleton site-wide settings: name, contact, social links, footer text.
 * Read by the public layout (footer) and by every public page that needs
 * contact info.
 */
@Entity
@Table(name = "site_settings")
public class SiteSettings {

    public static final Long SINGLETON_ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name")
    private String siteName;

    private String tagline;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "whatsapp")
    private String whatsApp;

    @Column(name = "banner_text")
    private String bannerText;

    @Column(name = "social_x")
    private String socialX;

    @Column(name = "social_instagram")
    private String socialInstagram;

    @Column(name = "social_youtube")
    private String socialYoutube;

    @Column(name = "social_telegram")
    private String socialTelegram;

    @Column(name = "marxist_url")
    private String marxistUrl;

    @Column(name = "books_url")
    private String booksUrl;

    @Column(name = "footer_description", columnDefinition = "TEXT")
    private String footerDescription;

    @Column(name = "footer_image_url", length = 1024)
    private String footerImageUrl;

    @Column(name = "copyright_line")
    private String copyrightLine;

    @Column(name = "closing_slogan")
    private String closingSlogan;

    @Column(name = "join_notification_email")
    private String joinNotificationEmail;

    protected SiteSettings() {
    }

    public static SiteSettings defaults() {
        SiteSettings s = defaultsTransient();
        s.id = SINGLETON_ID;
        return s;
    }

    public static SiteSettings defaultsTransient() {
        SiteSettings s = new SiteSettings();
        s.siteName = "Revolutionary Communists of Nigeria";
        s.tagline = "Nigerian Section of the Revolutionary Communist International";
        s.contactEmail = "contact@rcn.ng";
        s.whatsApp = "+234 800 000 0000";
        s.bannerText = "Nigerian Section of the Revolutionary Communist International · Workers of All Countries, Unite!";
        s.socialX = "https://x.com/rcn_ng";
        s.socialInstagram = "https://instagram.com/rcn_ng";
        s.socialYoutube = "https://youtube.com/@rcn_ng";
        s.socialTelegram = "https://t.me/rcn_ng";
        s.marxistUrl = "https://marxist.com";
        s.booksUrl = "https://www.marxistbooks.com/";
        s.footerDescription = "Nigerian section of the Revolutionary Communist International. "
                + "Fighting for a socialist Nigeria, for workers' power, for human liberation.";
        s.copyrightLine = "© 2026 Revolutionary Communists of Nigeria · Nigerian Section of the RCI";
        s.closingSlogan = "Workers of All Countries, Unite!";
        s.joinNotificationEmail = "akinrindeakinkunmi2006@gmail.com";
        return s;
    }

    // ---- Getters & setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }

    public String getBannerText() {
        return bannerText;
    }

    public void setBannerText(String bannerText) {
        this.bannerText = bannerText;
    }

    public String getSocialX() {
        return socialX;
    }

    public void setSocialX(String socialX) {
        this.socialX = socialX;
    }

    public String getSocialInstagram() {
        return socialInstagram;
    }

    public void setSocialInstagram(String socialInstagram) {
        this.socialInstagram = socialInstagram;
    }

    public String getSocialYoutube() {
        return socialYoutube;
    }

    public void setSocialYoutube(String socialYoutube) {
        this.socialYoutube = socialYoutube;
    }

    public String getSocialTelegram() {
        return socialTelegram;
    }

    public void setSocialTelegram(String socialTelegram) {
        this.socialTelegram = socialTelegram;
    }

    public String getMarxistUrl() {
        return marxistUrl;
    }

    public void setMarxistUrl(String marxistUrl) {
        this.marxistUrl = marxistUrl;
    }

    public String getBooksUrl() {
        return booksUrl;
    }

    public void setBooksUrl(String booksUrl) {
        this.booksUrl = booksUrl;
    }

    public String getFooterDescription() {
        return footerDescription;
    }

    public void setFooterDescription(String footerDescription) {
        this.footerDescription = footerDescription;
    }

    public String getFooterImageUrl() {
        return footerImageUrl;
    }

    public void setFooterImageUrl(String footerImageUrl) {
        this.footerImageUrl = footerImageUrl;
    }

    public String getCopyrightLine() {
        return copyrightLine;
    }

    public void setCopyrightLine(String copyrightLine) {
        this.copyrightLine = copyrightLine;
    }

    public String getClosingSlogan() {
        return closingSlogan;
    }

    public void setClosingSlogan(String closingSlogan) {
        this.closingSlogan = closingSlogan;
    }

    public String getJoinNotificationEmail() {
        return joinNotificationEmail;
    }

    public void setJoinNotificationEmail(String joinNotificationEmail) {
        this.joinNotificationEmail = joinNotificationEmail;
    }
}
