package com.example.rcn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Singleton row of editable homepage content. There is only ever one record
 * (id = {@link #SINGLETON_ID}); every CMS edit updates that same row.
 */
@Entity
@Table(name = "homepage_content")
public class HomepageContent {

    /** The one and only row's primary key. */
    public static final Long SINGLETON_ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Hero section ----
    @Column(name = "hero_headline")
    private String heroHeadline;

    @Column(name = "hero_subtext", columnDefinition = "TEXT")
    private String heroSubtext;

    /** Cloudinary secure_url for the hero background image. */
    @Column(name = "hero_image", length = 1024)
    private String heroImage;

    @Column(name = "hero_primary_button_text")
    private String heroPrimaryButtonText;

    @Column(name = "hero_primary_button_url")
    private String heroPrimaryButtonUrl;

    @Column(name = "hero_secondary_button_text")
    private String heroSecondaryButtonText;

    @Column(name = "hero_secondary_button_url")
    private String heroSecondaryButtonUrl;

    // ---- Scrolling ticker ----
    @Column(name = "ticker_line_1")
    private String tickerLine1;

    @Column(name = "ticker_line_2")
    private String tickerLine2;

    @Column(name = "ticker_line_3")
    private String tickerLine3;

    @Column(name = "ticker_line_4")
    private String tickerLine4;

    @Column(name = "ticker_line_5")
    private String tickerLine5;

    // ---- Featured article ----
    @Column(name = "featured_article_title")
    private String featuredArticleTitle;

    @Column(name = "featured_article_body", columnDefinition = "TEXT")
    private String featuredArticleBody;

    @Column(name = "featured_article_date")
    private String featuredArticleDate;

    @Column(name = "featured_article_url")
    private String featuredArticleUrl;

    // ---- Homepage article / podcast / activity selections ----
    /** Id of the article featured in the hero (optional). */
    @Column(name = "featured_article_id")
    private Long featuredArticleId;

    /** Comma-separated ids of up to 6 additional articles shown on the homepage. */
    @Column(name = "homepage_article_ids")
    private String homepageArticleIds = "";

    /** Comma-separated ids of up to 4 podcasts shown in the homepage podcast section. */
    @Column(name = "homepage_podcast_ids")
    private String homepagePodcastIds = "";

    /** Comma-separated ids of up to 3 activities shown in the homepage activity section. */
    @Column(name = "homepage_activity_ids")
    private String homepageActivityIds = "";

    /** Cloudinary URLs for the per-section images added in Part A. */
    @Column(name = "featured_article_image_url", length = 1024)
    private String featuredArticleImageUrl = "";

    @Column(name = "podcast_section_image_url", length = 1024)
    private String podcastSectionImageUrl = "";

    @Column(name = "activity_section_image_url", length = 1024)
    private String activitySectionImageUrl = "";

    @Column(name = "ticker_background_image_url", length = 1024)
    private String tickerBackgroundImageUrl = "";

    // ---- Statistics strip ----
    @Column(name = "stat_1_number")
    private String stat1Number;

    @Column(name = "stat_1_label")
    private String stat1Label;

    @Column(name = "stat_2_number")
    private String stat2Number;

    @Column(name = "stat_2_label")
    private String stat2Label;

    @Column(name = "stat_3_number")
    private String stat3Number;

    @Column(name = "stat_3_label")
    private String stat3Label;

    protected HomepageContent() {
        // JPA requires a no-arg constructor.
    }

    /**
     * Returns a fresh instance seeded with the current live-site copy, so the
     * public homepage shows real content on first boot before the CMS has been
     * touched.
     */
    public static HomepageContent defaults() {
        HomepageContent c = defaultsTransient();
        c.id = SINGLETON_ID;
        return c;
    }

    /**
     * Like {@link #defaults()}, but leaves the id unset so the instance is
     * transient. Use this when the row does not exist in the database yet —
     * saving it triggers a persist() and the DB assigns id = 1 via the identity
     * column. (Saving a detached instance would run merge() and fail.)
     */
    public static HomepageContent defaultsTransient() {
        HomepageContent c = new HomepageContent();

        // Hero
        c.heroHeadline = "Workers of Nigeria, Unite.";
        c.heroSubtext = "Nigeria's oil wealth feeds billionaires while millions go hungry. "
                + "The naira collapses, fuel costs a fortune, and politicians loot without consequence. "
                + "This is not mismanagement — it is capitalism working exactly as designed. The RCN says: enough.";
        c.heroImage = "";
        c.heroPrimaryButtonText = "Join the Revolution →";
        c.heroPrimaryButtonUrl = "/join";
        c.heroSecondaryButtonText = "Read Analysis";
        c.heroSecondaryButtonUrl = "/news";

        // Ticker
        c.tickerLine1 = "NLC general strikes have been betrayed — the trade union bureaucracy must be held accountable";
        c.tickerLine2 = "Only a workers' government can end ASUU strikes, fuel scarcity, and poverty wages";
        c.tickerLine3 = "Nigeria's ruling class has looted over $1 trillion since independence";
        c.tickerLine4 = "Youth unemployment stands above 53% — the highest in decades";
        c.tickerLine5 = "The RCN is the Nigerian section of the Revolutionary Communist International";

        // Featured article
        c.featuredArticleTitle = "The Tinubu Subsidy Removal: Who Really Paid?";
        c.featuredArticleBody = "When President Tinubu announced the removal of fuel subsidies, the IMF applauded. "
                + "Ordinary Nigerians experienced it as catastrophe — transport costs tripled overnight, food prices surged, "
                + "and millions already living on the edge were pushed off it. But who was the subsidy really for? "
                + "Marxist economics gives us the answer.";
        c.featuredArticleDate = "Economy · June 2026";
        c.featuredArticleUrl = "/article";

        // Homepage selections
        c.featuredArticleId = null;
        c.homepageArticleIds = "";
        c.homepagePodcastIds = "";
        c.homepageActivityIds = "";
        c.featuredArticleImageUrl = "";
        c.podcastSectionImageUrl = "";
        c.activitySectionImageUrl = "";
        c.tickerBackgroundImageUrl = "";

        // Stats
        c.stat1Number = "133M+";
        c.stat1Label = "Nigerians below poverty line";
        c.stat2Number = "53%";
        c.stat2Label = "Youth unemployment rate";
        c.stat3Number = "$1T+";
        c.stat3Label = "Looted since independence";

        return c;
    }

    // ---- Getters & setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeroHeadline() {
        return heroHeadline;
    }

    public void setHeroHeadline(String heroHeadline) {
        this.heroHeadline = heroHeadline;
    }

    public String getHeroSubtext() {
        return heroSubtext;
    }

    public void setHeroSubtext(String heroSubtext) {
        this.heroSubtext = heroSubtext;
    }

    public String getHeroImage() {
        return heroImage;
    }

    public void setHeroImage(String heroImage) {
        this.heroImage = heroImage;
    }

    public String getHeroPrimaryButtonText() {
        return heroPrimaryButtonText;
    }

    public void setHeroPrimaryButtonText(String heroPrimaryButtonText) {
        this.heroPrimaryButtonText = heroPrimaryButtonText;
    }

    public String getHeroPrimaryButtonUrl() {
        return heroPrimaryButtonUrl;
    }

    public void setHeroPrimaryButtonUrl(String heroPrimaryButtonUrl) {
        this.heroPrimaryButtonUrl = heroPrimaryButtonUrl;
    }

    public String getHeroSecondaryButtonText() {
        return heroSecondaryButtonText;
    }

    public void setHeroSecondaryButtonText(String heroSecondaryButtonText) {
        this.heroSecondaryButtonText = heroSecondaryButtonText;
    }

    public String getHeroSecondaryButtonUrl() {
        return heroSecondaryButtonUrl;
    }

    public void setHeroSecondaryButtonUrl(String heroSecondaryButtonUrl) {
        this.heroSecondaryButtonUrl = heroSecondaryButtonUrl;
    }

    public String getTickerLine1() {
        return tickerLine1;
    }

    public void setTickerLine1(String tickerLine1) {
        this.tickerLine1 = tickerLine1;
    }

    public String getTickerLine2() {
        return tickerLine2;
    }

    public void setTickerLine2(String tickerLine2) {
        this.tickerLine2 = tickerLine2;
    }

    public String getTickerLine3() {
        return tickerLine3;
    }

    public void setTickerLine3(String tickerLine3) {
        this.tickerLine3 = tickerLine3;
    }

    public String getTickerLine4() {
        return tickerLine4;
    }

    public void setTickerLine4(String tickerLine4) {
        this.tickerLine4 = tickerLine4;
    }

    public String getTickerLine5() {
        return tickerLine5;
    }

    public void setTickerLine5(String tickerLine5) {
        this.tickerLine5 = tickerLine5;
    }

    public String getFeaturedArticleTitle() {
        return featuredArticleTitle;
    }

    public void setFeaturedArticleTitle(String featuredArticleTitle) {
        this.featuredArticleTitle = featuredArticleTitle;
    }

    public String getFeaturedArticleBody() {
        return featuredArticleBody;
    }

    public void setFeaturedArticleBody(String featuredArticleBody) {
        this.featuredArticleBody = featuredArticleBody;
    }

    public String getFeaturedArticleDate() {
        return featuredArticleDate;
    }

    public void setFeaturedArticleDate(String featuredArticleDate) {
        this.featuredArticleDate = featuredArticleDate;
    }

    public String getFeaturedArticleUrl() {
        return featuredArticleUrl;
    }

    public void setFeaturedArticleUrl(String featuredArticleUrl) {
        this.featuredArticleUrl = featuredArticleUrl;
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

    // ---- Homepage selection fields ----

    public Long getFeaturedArticleId() {
        return featuredArticleId;
    }

    public void setFeaturedArticleId(Long featuredArticleId) {
        this.featuredArticleId = featuredArticleId;
    }

    public String getHomepageArticleIds() {
        return homepageArticleIds;
    }

    public void setHomepageArticleIds(String homepageArticleIds) {
        this.homepageArticleIds = homepageArticleIds;
    }

    public String getHomepagePodcastIds() {
        return homepagePodcastIds;
    }

    public void setHomepagePodcastIds(String homepagePodcastIds) {
        this.homepagePodcastIds = homepagePodcastIds;
    }

    public String getHomepageActivityIds() {
        return homepageActivityIds;
    }

    public void setHomepageActivityIds(String homepageActivityIds) {
        this.homepageActivityIds = homepageActivityIds;
    }

    public String getFeaturedArticleImageUrl() {
        return featuredArticleImageUrl;
    }

    public void setFeaturedArticleImageUrl(String featuredArticleImageUrl) {
        this.featuredArticleImageUrl = featuredArticleImageUrl;
    }

    public String getPodcastSectionImageUrl() {
        return podcastSectionImageUrl;
    }

    public void setPodcastSectionImageUrl(String podcastSectionImageUrl) {
        this.podcastSectionImageUrl = podcastSectionImageUrl;
    }

    public String getActivitySectionImageUrl() {
        return activitySectionImageUrl;
    }

    public void setActivitySectionImageUrl(String activitySectionImageUrl) {
        this.activitySectionImageUrl = activitySectionImageUrl;
    }

    public String getTickerBackgroundImageUrl() {
        return tickerBackgroundImageUrl;
    }

    public void setTickerBackgroundImageUrl(String tickerBackgroundImageUrl) {
        this.tickerBackgroundImageUrl = tickerBackgroundImageUrl;
    }

    /**
     * Splits the stored {@code homepageArticleIds} ("1,2,3") into a list of ids.
     * Returns an empty list when unset.
     */
    public java.util.List<Long> getHomepageArticleIdList() {
        return idListFrom(homepageArticleIds);
    }

    public java.util.List<Long> getHomepagePodcastIdList() {
        return idListFrom(homepagePodcastIds);
    }

    public java.util.List<Long> getHomepageActivityIdList() {
        return idListFrom(homepageActivityIds);
    }

    private static java.util.List<Long> idListFrom(String csv) {
        if (csv == null || csv.isBlank()) {
            return java.util.List.of();
        }
        return java.util.Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .toList();
    }
}
